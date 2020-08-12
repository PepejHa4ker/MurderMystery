  
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

package pl.plajer.murdermystery.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.utils.MessageUtils;
import pl.plajer.murdermystery.utils.config.ConfigUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;

/**
 * @author Plajer
 * @since 0.0.1-alpha
 * <p>
 * Class for accessing users statistics.
 */
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StatsStorage {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);

    private static Map sortByValue(Map<?, ?> unsortMap) {
        List list = new LinkedList<>(unsortMap.entrySet());
        list.sort((o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue()));
        Map sortedMap = new LinkedHashMap();
        for (Object sort : list) {
            Map.Entry entry = (Map.Entry) sort;
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    /**
     * Get all UUID's sorted ascending by Statistic Type
     *
     * @param stat Statistic type to get (kills, deaths etc.)
     * @return Map of UUID keys and Integer values sorted in ascending order of requested statistic type
     */
    @Contract("null -> fail")
    public static Map getStats(StatisticType stat) {
        if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
            try (Connection connection = plugin.getDatabase().getConnection()) {
                Statement statement = connection.createStatement();
                ResultSet set = statement.executeQuery("SELECT UUID, " + stat.getName() + " FROM playerstats ORDER BY " + stat.getName());
                Map<java.util.UUID, java.lang.Integer> column = new LinkedHashMap<>();
                while (set.next()) {
                    column.put(java.util.UUID.fromString(set.getString("UUID")), set.getInt(stat.getName()));
                }
                return column;
            } catch (SQLException e) {
                plugin.getLogger().log(Level.WARNING, "SQLException occurred! " + e.getSQLState() + " (" + e.getErrorCode() + ")");
                MessageUtils.errorOccurred();
                Bukkit.getConsoleSender().sendMessage("Cannot get contents from MySQL database!");
                Bukkit.getConsoleSender().sendMessage("Check configuration of mysql.yml file or disable mysql option in config.yml");
                return Collections.emptyMap();
            }
        }
        FileConfiguration config = ConfigUtils.getConfig(plugin, "stats");
        Map<UUID, Integer> stats = new TreeMap<>();
        for (String string : config.getKeys(false)) {
            if (string.equals("data-version")) {
                continue;
            }
            stats.put(UUID.fromString(string), config.getInt(string + "." + stat.getName()));
        }
        return sortByValue(stats);
    }

    /**
     * Get user statistic based on StatisticType
     *
     * @param player        Online player to get data from
     * @param statisticType Statistic type to get (kills, deaths etc.)
     * @return int of statistic
     * @see StatisticType
     */
    public static int getUserStats(Player player, StatisticType statisticType) {
        return plugin.getUserManager().getUser(player).getStat(statisticType);
    }

    /**
     * Available statistics to get.
     */
    public enum StatisticType {
        @Deprecated //subject to remove and merge with randomized game points
                CONTRIBUTION_DETECTIVE("contribdetective", true, ""),
        @Deprecated //subject to remove and merge with randomized game points
                CONTRIBUTION_MURDERER("contribmurderer", true, ""), DEATHS("deaths", true, "Смертей"), GAMES_PLAYED("gamesplayed", true, "Игр сыграно"), HIGHEST_SCORE("highestscore", true, "Очков"),
        KILLS("kills", true, "Убийств"), LOSES("loses", true, "Поражений"), WINS("wins", true, "Побед"), LOCAL_CURRENT_PRAY("local_pray", false, "Молитва"), LOCAL_GOLD("gold", false, "Золота"), LOCAL_KILLS("local_kills", false, "Убийств"),
        LOCAL_PRAISES("local_praises", false, "Молитв"), LOCAL_SCORE("local_score", false, "Очков"), KARMA("karma", true, "Кармы");

        @Getter
        private String name;
        @Getter
        private boolean persistent;
        @Getter
        private String formattedName;

        StatisticType(String name, boolean persistent, String formattedName) {
            this.name = name;
            this.persistent = persistent;
            this.formattedName = formattedName;
        }
    }
}
