

package com.pepej.murdermystery.events;

import com.pepej.murdermystery.ConfigPreferences;
import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.ArenaState;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.handlers.language.LanguageManager;
import com.pepej.murdermystery.user.User;
import com.pepej.murdermystery.utils.Utils;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;
import java.util.regex.Pattern;


public class ChatEvents implements Listener {

    private final MurderMystery plugin;
    private final String[] regexChars = new String[]{"$", "\\"};


    @Getter
    private final static Set<UUID> said = new ConcurrentSet<>();

    public ChatEvents(MurderMystery plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onChatIngame(AsyncPlayerChatEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        FileConfiguration filter = ConfigUtils.getConfig(plugin, "filter");
        if (arena == null) {
            return;
        }
        User user = plugin.getUserManager().getUser(event.getPlayer());
        if (event.getMessage().equalsIgnoreCase("gg") && arena.getArenaState() == ArenaState.ENDING) {
            if (!getSaid().contains(event.getPlayer().getUniqueId())) {
                int karma = Utils.getRandomNumber(10, 30);
                getSaid().add(event.getPlayer().getUniqueId());
                user.addStat(StatsStorage.StatisticType.KARMA, karma);
                event.getPlayer().sendMessage("§d+ " + karma + " к карме");
            }
        }
        if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.CHAT_FORMAT_ENABLED)) {
            event.setCancelled(true);
            Iterator<Player> it = event.getRecipients().iterator();
            List<Player> remove = new ArrayList<>();
            while (it.hasNext()) {
                Player player = it.next();
                remove.add(player);
            }
            for (Player player : remove) {
                event.getRecipients().remove(player);
            }
            remove.clear();
            String message;
            String eventMessage = event.getMessage();
            boolean dead = !arena.getPlayersLeft().contains(event.getPlayer());
            for (String regexChar : regexChars) {
                if (eventMessage.contains(regexChar)) {
                    eventMessage = eventMessage.replaceAll(Pattern.quote(regexChar), "");
                }
            }
            user.loadRank();

            message = formatChatPlaceholders(LanguageManager.getLanguageMessage("In-Game.Game-Chat-Format"), plugin.getUserManager().getUser(event.getPlayer()), eventMessage);
            List<String> wordList = filter.getStringList("words");

            if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("murder")) {
                for (String _word : wordList) {
                    if (event.getMessage().replaceAll("[^A-Za-zА-Яа-я]", "").contains(_word)) {
                        event.getPlayer().sendMessage("§cЭто слово нельзя использовать сейчас.");
                        event.setCancelled(true);
                        return;

                    }
                }
                for (Player player : arena.getPlayers()) {
                    if (dead && arena.getPlayersLeft().contains(player)) {
                        continue;
                    }
                    player.sendMessage(message);
                }
            }
            Bukkit.getConsoleSender().sendMessage(message);
        } else {
            event.getRecipients().clear();
            event.getRecipients().addAll(new ArrayList<>(arena.getPlayers()));
        }
    }

    private String formatChatPlaceholders(String message, User user, String saidMessage) {
        String formatted = message;
        formatted = ChatManager.colorRawMessage(formatted);
        formatted = StringUtils.replace(formatted, "%rank%", user.getRank().getName());
        formatted = StringUtils.replace(formatted, "%player%", user.getPlayer().getName());
        formatted = StringUtils.replace(formatted, "%message%", saidMessage);
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            formatted = PlaceholderAPI.setPlaceholders(user.getPlayer(), formatted);
        }
        return formatted;
    }

}
