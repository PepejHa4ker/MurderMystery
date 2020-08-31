package pl.plajer.murdermystery.handlers;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaManager;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.arena.ArenaState;
import pl.plajer.murdermystery.utils.config.ConfigUtils;

import java.util.EnumMap;
import java.util.Map;


public class BungeeManager implements Listener {

  private final MurderMystery plugin;
  private final Map<ArenaState, String> gameStateToString = new EnumMap<>(ArenaState.class);
  private final String MOTD;

  public BungeeManager(final MurderMystery plugin) {
    this.plugin = plugin;
    gameStateToString.put(ArenaState.WAITING_FOR_PLAYERS, ChatManager.colorRawMessage(ConfigUtils.getConfig(plugin, "bungee").getString("MOTD.Game-States.Inactive")));
    gameStateToString.put(ArenaState.STARTING, ChatManager.colorRawMessage(ConfigUtils.getConfig(plugin, "bungee").getString("MOTD.Game-States.Starting")));
    gameStateToString.put(ArenaState.IN_GAME, ChatManager.colorRawMessage(ConfigUtils.getConfig(plugin, "bungee").getString("MOTD.Game-States.In-Game")));
    gameStateToString.put(ArenaState.ENDING, ChatManager.colorRawMessage(ConfigUtils.getConfig(plugin, "bungee").getString("MOTD.Game-States.Ending")));
    gameStateToString.put(ArenaState.RESTARTING, ChatManager.colorRawMessage(ConfigUtils.getConfig(plugin, "bungee").getString("MOTD.Game-States.Restarting")));
    MOTD = ChatManager.colorRawMessage(ConfigUtils.getConfig(plugin, "bungee").getString("MOTD.Message"));
    plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  public void connectToHub(final Player player) {
    if (!ConfigUtils.getConfig(plugin, "bungee").getBoolean("Connect-To-Hub", true)) {
      return;
    }
    ByteArrayDataOutput out = ByteStreams.newDataOutput();
    out.writeUTF("Connect");
    out.writeUTF(getHubServerName());
    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
  }

  private ArenaState getArenaState() {
    Arena arena = ArenaRegistry.getArenas().get(0);
    return arena.getArenaState();
  }

  private String getHubServerName() {
    return ConfigUtils.getConfig(plugin, "bungee").getString("Hub");
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onServerListPing(ServerListPingEvent event) {
    if (!ConfigUtils.getConfig(plugin, "bungee").getBoolean("MOTD.Manager", false)) {
      return;
    }
    if (ArenaRegistry.getArenas().isEmpty()) {
      return;
    }
    event.setMaxPlayers(ArenaRegistry.getArenas().get(0).getMaximumPlayers());
    event.setMotd(MOTD.replace("%state%", gameStateToString.get(getArenaState())));
  }


  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(final PlayerJoinEvent event) {
    event.setJoinMessage("");
    plugin.getServer().getScheduler().runTaskLater(plugin, () -> ArenaManager.joinAttempt(event.getPlayer(), ArenaRegistry.getArenas().get(0)), 1L);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onQuit(PlayerQuitEvent event) {
    event.setQuitMessage("");
    if (ArenaRegistry.getArena(event.getPlayer()) != null) {
      ArenaManager.leaveAttempt(event.getPlayer(), ArenaRegistry.getArenas().get(0));
    }
  }

}
