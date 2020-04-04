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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import me.clip.placeholderapi.PlaceholderAPI;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.language.LanguageManager;
import pl.plajer.murdermystery.user.User;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 03.08.2018
 */
public class ChatEvents implements Listener {

  private Main plugin;
  private String[] regexChars = new String[] {"$", "\\"};
  private String rank;

  public ChatEvents(Main plugin) {
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
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.CHAT_FORMAT_ENABLED)) {
      event.setCancelled(true);
      Iterator<Player> iterator = event.getRecipients().iterator();
      List<Player> remove = new ArrayList<>();
      while (iterator.hasNext()) {
        Player player = iterator.next();
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
      int xp = plugin.getUserManager().getUser(event.getPlayer()).getStat(StatsStorage.StatisticType.HIGHEST_SCORE);
      FileConfiguration ranks = ConfigUtils.getConfig(plugin, "ranks");
      String novobranec = ranks.getString("ranks.Новобранец.name").replace('&', '§');
      int xpNeededNovobranec = ranks.getInt("ranks.Новобранец.xp");
      if(xp >= xpNeededNovobranec) {
        rank = novobranec;
      }
      String meet = ranks.getString("ranks.Мясо.name").replace('&', '§');
      int xpNeededMeet = ranks.getInt("ranks.Мясо.xp");
      if(xp >= xpNeededMeet) {
        rank = meet;
      }
      String harmless = ranks.getString("ranks.Безобидный.name").replace('&', '§');
      int xpNeededHarmless = ranks.getInt("ranks.Безобидный.xp");
      if(xp >= xpNeededHarmless) {
          rank = harmless;
      }
      String expert = ranks.getString("ranks.Знаток.name").replace('&', '§');
      int xpNeededExpert = ranks.getInt("ranks.Знаток.xp");
      if(xp >= xpNeededExpert) {
        rank = expert;
      }
      String progressor = ranks.getString("ranks.Прогрессор.name").replace('&', '§');
      int xpNeededProgressor = ranks.getInt("ranks.Безобидный.xp");
      if(xp >= xpNeededProgressor) {
        rank = progressor;
      }
      String scarecrow = ranks.getString("ranks.Знаток.name").replace('&', '§');
      int xpNeededScarecrow = ranks.getInt("ranks.Знаток.xp");
      if(xp >= xpNeededScarecrow) {
        rank = scarecrow;
      }
      String knight = ranks.getString("ranks.Воин.name").replace('&', '§');
      int xpNeededKnight = ranks.getInt("ranks.Воин.xp");
      if(xp >= xpNeededKnight) {
        rank = knight;
      }
      String sinner = ranks.getString("ranks.Грешник.name").replace('&', '§');
      int xpNeededSinner = ranks.getInt("ranks.Грешник.xp");
      if(xp >= xpNeededSinner) {
        rank = sinner;
      }
      String shadow = ranks.getString("ranks.Тень.name").replace('&', '§');
      int xpNeededShadow = ranks.getInt("ranks.Тень.xp");
      if(xp >= xpNeededShadow) {
        rank = shadow;
      }
      String killer = ranks.getString("ranks.Убийца.name").replace('&', '§');
      int xpNeededKiller = ranks.getInt("ranks.Убийца.xp");
      if(xp >= xpNeededKiller) {
        rank = killer;
      }
      message = formatChatPlaceholders(LanguageManager.getLanguageMessage("In-Game.Game-Chat-Format"), plugin.getUserManager().getUser(event.getPlayer()), eventMessage);
      for (Player player : arena.getPlayers()) {
        if (dead && arena.getPlayersLeft().contains(player)) {
          continue;
        }
        String _message = event.getMessage();
        String[] words = _message.split(" ");
        List<String> wordList = filter.getStringList("words");
        if (!player.getWorld().getName().equalsIgnoreCase("murder")) {
          for (String word : words) {
            if (wordList.contains(word)) {
              player.sendMessage("§cЭто слово нельзя использовать сейчас.");;
              return;
            }
          }
        }
        player.sendMessage(message);
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
    formatted = StringUtils.replace(formatted, "%rank%", rank);
    formatted = StringUtils.replace(formatted, "%player%", user.getPlayer().getName());
    formatted = StringUtils.replace(formatted, "%message%", saidMessage);
    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      formatted = PlaceholderAPI.setPlaceholders(user.getPlayer(), formatted);
    }
    return formatted;
  }

}
