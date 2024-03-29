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

package com.pepej.murdermystery.user.data;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.user.User;
import com.pepej.murdermystery.utils.database.MysqlDatabase;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Plajer
 * <p>
 * Created at 03.10.2018
 */
public class MysqlManager implements UserDatabase {

  private final MurderMystery plugin;
  @Getter
  private final MysqlDatabase database;

  public MysqlManager(MurderMystery plugin) {
    this.plugin = plugin;
    database = plugin.getDatabase();
    Bukkit.getScheduler().runTask(plugin, () -> {
      try (Connection connection = database.getConnection()) {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `playerstats` (\n"
          + "  `UUID` char(36) NOT NULL PRIMARY KEY,\n"
          + "  `name` varchar(32) NOT NULL,\n"
          + "  `kills` int(11) NOT NULL DEFAULT '0',\n"
          + "  `deaths` int(11) NOT NULL DEFAULT '0',\n"
          + "  `highestscore` int(11) NOT NULL DEFAULT '0',\n"
          + "  `karma` int(11) NOT NULL DEFAULT '0',\n"
          + "  `gamesplayed` int(11) NOT NULL DEFAULT '0',\n"
          + "  `wins` int(11) NOT NULL DEFAULT '0',\n"
          + "  `loses` int(11) NOT NULL DEFAULT '0',\n"
          + "  `contribmurderer` int(11) NOT NULL DEFAULT '1',\n"
          + "  `contribdetective` int(11) NOT NULL DEFAULT '1'\n"
          + "  `joinedtimes` int(11) NOT NULL DEFAULT '1'\n"
                + ") ENGINE=InnoDB;");
      } catch (SQLException e) {
        MurderMystery.getInstance().getPluginLogger().severe("Cannot save contents to MySQL database!");
        MurderMystery.getInstance().getPluginLogger().severe("Check configuration of mysql.yml file or disable mysql option in config.yml");
      }
    });
  }

  @Override
  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    Bukkit.getScheduler().runTask(plugin, () ->
            database.executeUpdate("UPDATE playerstats SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';"));
  }

  @Override
  public void loadStatistics(User user) {
    Bukkit.getScheduler().runTask(plugin, () -> {
      String uuid = user.getPlayer().getUniqueId().toString();
      try (Connection connection = database.getConnection()) {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM playerstats WHERE UUID='" + uuid + "'");
        if (rs.next()) {
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.getNonPersistentStats()) {
            user.setStat(stat, rs.getInt(stat.getName()));
          }
        } else {
          //player doesn't exist - make a new record
          statement.executeUpdate("INSERT INTO playerstats (UUID, name) VALUES ('" + uuid + "','" + user.getPlayer().getName() + "')");
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.getNonPersistentStats()) {
            user.setStat(stat, 0);
          }
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
  }

}
