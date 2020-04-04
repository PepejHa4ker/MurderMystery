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

package pl.plajer.murdermystery.user;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.user.data.FileStats;
import pl.plajer.murdermystery.user.data.MysqlManager;
import pl.plajer.murdermystery.user.data.UserDatabase;
import pl.plajer.murdermystery.utils.Debugger;

/**
 * @author Plajer
 * <p>
 * Created at 03.08.2018
 */
public class UserManager {

  private UserDatabase database;
  private List<User> users = new ArrayList<>();

  public UserManager(Main plugin) {
    if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
      database = new MysqlManager(plugin);
    } else {
      database = new FileStats(plugin);
    }
    loadStatsForPlayersOnline();
  }

  private void loadStatsForPlayersOnline() {
    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
      User user = getUser(player);
      loadStatistics(user);
    }
  }

  public User getUser(Player player) {
    for (User user : users) {
      if (user.getPlayer().equals(player)) {
        return user;
      }
    }
    Debugger.debug(Level.INFO, "Registering new user {0} ({1})", player.getUniqueId(), player.getName());
    User user = new User(player);
    users.add(user);
    return user;
  }

  public List<User> getUsers(Arena arena) {
    List<User> users = new ArrayList<>();
    for (Player player : arena.getPlayers()) {
      users.add(getUser(player));
    }
    return users;
  }

  public void saveStatistic(User user, StatsStorage.StatisticType stat) {
    if (!stat.isPersistent()) {
      return;
    }
    //apply before save
    fixContirbutionStatistics(user);
    database.saveStatistic(user, stat);
  }

  public void loadStatistics(User user) {
    database.loadStatistics(user);
    //apply after load to override
    fixContirbutionStatistics(user);
  }

  private void fixContirbutionStatistics(User user) {
    if (user.getStat(StatsStorage.StatisticType.CONTRIBUTION_DETECTIVE) <= 0) {
      user.setStat(StatsStorage.StatisticType.CONTRIBUTION_DETECTIVE, 1);
    }
    if (user.getStat(StatsStorage.StatisticType.CONTRIBUTION_MURDERER) <= 0) {
      user.setStat(StatsStorage.StatisticType.CONTRIBUTION_MURDERER, 1);
    }
  }

  public void removeUser(User user) {
    users.remove(user);
  }

  public UserDatabase getDatabase() {
    return database;
  }
}
