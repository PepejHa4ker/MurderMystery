/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.murdermystery.events;

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
import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.arena.ArenaState;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.language.LanguageManager;
import pl.plajer.murdermystery.user.User;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.config.ConfigUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Plajer
 * <p>
 * Created at 03.08.2018
 */
@SuppressWarnings("ALL")
public class ChatEvents implements Listener {

    private MurderMystery plugin;
    private String[] regexChars = new String[]{"$", "\\"};


    @Getter
    private static Set<UUID> said = new ConcurrentSet<>();

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
