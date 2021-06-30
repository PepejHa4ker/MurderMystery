package com.pepej.murdermystery.user;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.user.data.MysqlManager;
import com.pepej.murdermystery.user.data.UserDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class UserManager {

    private final UserDatabase database;
    private final List<User> users = new ArrayList<>();

    public UserManager(MurderMystery plugin) {
        database = new MysqlManager(plugin);
        MurderMystery.getInstance().getPluginLogger().info("MySQL connected!");
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
        if (user.getStat(StatsStorage.StatisticType.CONTRIBUTION_MEDIC) <= 0) {
            user.setStat(StatsStorage.StatisticType.CONTRIBUTION_MEDIC, 1);
        }
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public UserDatabase getDatabase() {
        return database;
    }
}
