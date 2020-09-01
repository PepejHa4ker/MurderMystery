
package com.pepej.murdermystery.handlers.sign;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaManager;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.ArenaState;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.handlers.language.LanguageManager;
import com.pepej.murdermystery.utils.compat.XMaterial;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import com.pepej.murdermystery.utils.serialization.LocationSerializer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class SignManager implements Listener {

    private final MurderMystery plugin;
    private final List<ArenaSign> arenaSigns = new ArrayList<>();
    private final Map<ArenaState, String> gameStateToString = new EnumMap<>(ArenaState.class);
    private final List<String> signLines;

    public SignManager(MurderMystery plugin) {
        this.plugin = plugin;
        gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, ChatManager.colorMessage("Signs.Game-States.Inactive"));
        gameStateToString.put(ArenaState.STARTING, ChatManager.colorMessage("Signs.Game-States.Starting"));
        gameStateToString.put(ArenaState.IN_GAME, ChatManager.colorMessage("Signs.Game-States.In-Game"));
        gameStateToString.put(ArenaState.ENDING, ChatManager.colorMessage("Signs.Game-States.Ending"));
        gameStateToString.put(ArenaState.RESTARTING, ChatManager.colorMessage("Signs.Game-States.Restarting"));
        signLines = LanguageManager.getLanguageList("Signs.Lines");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadSigns();
        updateSignScheduler();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        if (!e.getPlayer().hasPermission("murdermystery.admin.sign.create")
                || !e.getLine(0).equalsIgnoreCase("[murdermystery]")) {
            return;
        }
        if (e.getLine(1).isEmpty()) {
            e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Please-Type-Arena-Name"));
            return;
        }
        for (Arena arena : ArenaRegistry.getArenas()) {
            if (!arena.getId().equalsIgnoreCase(e.getLine(1))) {
                continue;
            }
            for (int i = 0; i < signLines.size(); i++) {
                e.setLine(i, formatSign(signLines.get(i), arena));
            }
            arenaSigns.add(new ArenaSign((Sign) e.getBlock().getState(), arena));
            e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Sign-Created"));
            String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + ",0.0,0.0";
            FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
            List<String> locs = config.getStringList("instances." + arena.getId() + ".signs");
            locs.add(location);
            config.set("instances." + arena.getId() + ".signs", locs);
            ConfigUtils.saveConfig(plugin, config, "arenas");
            return;
        }
        e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Arena-Doesnt-Exists"));
    }

    private String formatSign(String msg, Arena a) {
        String formatted = msg;
        formatted = StringUtils.replace(formatted, "%mapname%", a.getMapName());
        if (a.getPlayers().size() >= a.getMaximumPlayers()) {
            formatted = StringUtils.replace(formatted, "%state%", ChatManager.colorMessage("Signs.Game-States.Full-Game"));
        } else {
            formatted = StringUtils.replace(formatted, "%state%", gameStateToString.get(a.getArenaState()));
        }
        formatted = StringUtils.replace(formatted, "%playersize%", String.valueOf(a.getPlayers().size()));
        formatted = StringUtils.replace(formatted, "%maxplayers%", String.valueOf(a.getMaximumPlayers()));
        formatted = ChatManager.colorRawMessage(formatted);
        return formatted;
    }

    @EventHandler
    public void onSignDestroy(BlockBreakEvent e) {
        ArenaSign arenaSign = getArenaSignByBlock(e.getBlock());
        if (!e.getPlayer().hasPermission("murdermystery.admin.sign.break") || arenaSign == null) {
            return;
        }
        arenaSigns.remove(arenaSign);
        FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
        String location = e.getBlock().getWorld().getName() + "," + e.getBlock().getX() + "," + e.getBlock().getY() + "," + e.getBlock().getZ() + "," + "0.0,0.0";
        for (String arena : config.getConfigurationSection("instances").getKeys(false)) {
            for (String sign : config.getStringList("instances." + arena + ".signs")) {
                if (!sign.equals(location)) {
                    continue;
                }
                List<String> signs = config.getStringList("instances." + arena + ".signs");
                signs.remove(location);
                config.set(arena + ".signs", signs);
                ConfigUtils.saveConfig(plugin, config, "arenas");
                e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Sign-Removed"));
                return;
            }
        }
        e.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatColor.RED + "Couldn't remove sign from configuration! Please do this manually!");
    }

    @EventHandler
    public void onJoinAttempt(PlayerInteractEvent e) {
        ArenaSign arenaSign = getArenaSignByBlock(e.getClickedBlock());
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && e.getClickedBlock().getState() instanceof Sign && arenaSign != null) {
            Arena arena = arenaSign.getArena();
            if (arena == null) {
                return;
            }
            ArenaManager.joinAttempt(e.getPlayer(), arena);
        }
    }

    @Nullable
    private ArenaSign getArenaSignByBlock(Block block) {
        if (block == null) {
            return null;
        }
        ArenaSign arenaSign = null;
        for (ArenaSign sign : arenaSigns) {
            if (sign.getSign().getLocation().equals(block.getLocation())) {
                arenaSign = sign;
                break;
            }
        }
        return arenaSign;
    }

    public void loadSigns() {

        arenaSigns.clear();
        FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
        for (String path : config.getConfigurationSection("instances").getKeys(false)) {
            for (String sign : config.getStringList("instances." + path + ".signs")) {
                Location loc = LocationSerializer.getLocation(sign);
                if (loc.getBlock().getState() instanceof Sign) {
                    arenaSigns.add(new ArenaSign((Sign) loc.getBlock().getState(), ArenaRegistry.getArena(path)));
                }
            }
        }
    }

    private void updateSignScheduler() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (ArenaSign arenaSign : arenaSigns) {
                Sign sign = arenaSign.getSign();
                for (int i = 0; i < signLines.size(); i++) {
                    sign.setLine(i, formatSign(signLines.get(i), arenaSign.getArena()));
                }
                if (plugin.getConfig().getBoolean("Signs-Block-States-Enabled", true) && arenaSign.getBehind() != null) {
                    Block behind = arenaSign.getBehind();
                    switch (arenaSign.getArena().getArenaState()) {
                        case WAITING_FOR_PLAYERS:
                            behind.setType(XMaterial.WHITE_STAINED_GLASS.parseMaterial());
                            behind.setData((byte) 0);
                            break;
                        case STARTING:
                            behind.setType(XMaterial.YELLOW_STAINED_GLASS.parseMaterial());
                            behind.setData((byte) 4);
                            break;
                        case IN_GAME:
                            behind.setType(XMaterial.ORANGE_STAINED_GLASS.parseMaterial());
                            behind.setData((byte) 1);
                            break;
                        case ENDING:
                            behind.setType(XMaterial.GRAY_STAINED_GLASS.parseMaterial());
                            behind.setData((byte) 7);

                            break;
                        case RESTARTING:
                            behind.setType(XMaterial.BLACK_STAINED_GLASS.parseMaterial());
                            behind.setData((byte) 15);
                            break;
                        default:
                            break;
                    }
                }
                sign.update();
            }
        }, 10, 10);
    }

    public List<ArenaSign> getArenaSigns() {
        return arenaSigns;
    }
}
