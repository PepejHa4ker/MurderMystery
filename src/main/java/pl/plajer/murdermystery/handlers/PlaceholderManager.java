package pl.plajer.murdermystery.handlers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaRegistry;


public class PlaceholderManager extends PlaceholderExpansion {

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "murdermystery";
    }

    @Override
    public String getAuthor() {
        return "Plajer";
    }

    @Override
    public String getVersion() {
        return "1.0.1";
    }

    @Override
    public String onPlaceholderRequest(Player player, String id) {
        if (player == null) {
            return null;
        }
        switch (id.toLowerCase()) {
            case "kills":
                return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.KILLS));
            case "deaths":
                return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.DEATHS));
            case "games_played":
                return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.GAMES_PLAYED));
            case "highest_score":
                return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.HIGHEST_SCORE));
            case "wins":
                return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.WINS));
            case "loses":
                return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.LOSES));
            case "karma":
                return String.valueOf(StatsStorage.getUserStats(player, StatsStorage.StatisticType.KARMA));
            case "rank":
                return MurderMystery.getInstance().getUserManager().getUser(player).getRank().getName().replaceAll("[^А-Яа-я]", "");
            default:
                return handleArenaPlaceholderRequest(id);
        }
    }

    private String handleArenaPlaceholderRequest(String id) {
        if (!id.contains(":")) {
            return null;
        }
        String[] data = id.split(":");
        Arena arena = ArenaRegistry.getArena(data[0]);
        if (arena == null) {
            return null;
        }
        switch (data[1].toLowerCase()) {
            case "time":
                return String.valueOf(arena.getTimer());
            case "min_players":
                return String.valueOf(arena.getMinimumPlayers());
            case "players":
                return String.valueOf(arena.getPlayers().size());
            case "max_players":
                return String.valueOf(arena.getMaximumPlayers());
            case "state":
                return String.valueOf(arena.getArenaState());
            case "state_pretty":
                return arena.getArenaState().getFormattedName();
            case "mapname":
                return arena.getMapName();
            default:
                return null;
        }
    }

}