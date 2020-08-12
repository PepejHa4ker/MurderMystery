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

package pl.plajer.murdermystery.commands.arguments.game;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.commands.arguments.ArgumentsRegistry;
import pl.plajer.murdermystery.commands.arguments.data.CommandArgument;
import pl.plajer.murdermystery.commands.completion.CompletableArgument;
import pl.plajer.murdermystery.handlers.ChatManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author Plajer
 * <p>
 * Created at 18.05.2019
 */
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)

public class LeaderboardArgument {

  ArgumentsRegistry registry;
  public LeaderboardArgument(ArgumentsRegistry registry) {
    this.registry = registry;
    List<String> stats = new ArrayList<>();
    for (StatsStorage.StatisticType value : StatsStorage.StatisticType.values()) {
      if (!value.isPersistent()) {
        continue;
      }
      stats.add(value.name().toLowerCase());
    }
    registry.getTabCompletion().registerCompletion(new CompletableArgument("murdermystery", "top", stats));
    registry.mapArgument("murdermystery", new CommandArgument("top", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX +  ChatManager.colorMessage("Commands.Statistics.Type-Name"));
          return;
        }
        try {
          StatsStorage.StatisticType statisticType = StatsStorage.StatisticType.valueOf(args[1].toUpperCase());
          if (!statisticType.isPersistent()) {
            sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Statistics.Invalid-Name"));
            return;
          }
          printLeaderboard(sender, statisticType);
        } catch (IllegalArgumentException e) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Statistics.Invalid-Name"));
        }
      }
    });
  }

  private void printLeaderboard(CommandSender sender, StatsStorage.StatisticType statisticType) {
    LinkedHashMap<UUID, Integer> stats = (LinkedHashMap<UUID, Integer>) StatsStorage.getStats(statisticType);
    sender.sendMessage(ChatManager.colorMessage("Commands.Statistics.Header"));
    String statistic = statisticType.getFormattedName();
    for (int i = 0; i < 10; i++) {
      try {
        UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
        sender.sendMessage(formatMessage(statistic, Bukkit.getOfflinePlayer(current).getName(), i + 1, stats.get(current)));
        stats.remove(current);
      } catch (IndexOutOfBoundsException ex) {
        sender.sendMessage(formatMessage(statistic, "Пусто", i + 1, 0));
      } catch (NullPointerException ex) {
        UUID current = (UUID) stats.keySet().toArray()[stats.keySet().toArray().length - 1];
        if (registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
          try (Connection connection = registry.getPlugin().getDatabase().getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT name FROM playerstats WHERE UUID='" + current.toString() + "'");
            if (set.next()) {
              sender.sendMessage(formatMessage(statistic, set.getString(1), i + 1, stats.get(current)));
              continue;
            }
          } catch (SQLException ignored) {
            //it has failed second time, cannot continue
          }
        }
        sender.sendMessage(formatMessage(statistic, "Неизвестный игрок", i + 1, stats.get(current)));
      }
    }
  }

  private String formatMessage(String statisticName, String playerName, int position, int value) {
    String message = ChatManager.colorMessage("Commands.Statistics.Format");
    message = StringUtils.replace(message, "%position%", String.valueOf(position));
    message = StringUtils.replace(message, "%name%", playerName);
    message = StringUtils.replace(message, "%value%", String.valueOf(value));
    message = StringUtils.replace(message, "%statistic%", statisticName);
    return message;
  }

}
