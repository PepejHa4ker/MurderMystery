

package pl.plajer.murdermystery.user;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.api.events.player.MMPlayerStatisticChangeEvent;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.perk.Perk;
import pl.plajer.murdermystery.utils.donate.DonatType;

import java.util.*;
import java.util.stream.Collectors;

public class User {
  private static final MurderMystery plugin = JavaPlugin.getPlugin(MurderMystery.class);
  private static long cooldownCounter = 0;
  private final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
  @Getter
  private final Player player;

  @Getter
  private final List<Perk> cachedPerks = new ArrayList<>();

  @Getter
  @Setter
  private boolean pickedPotion;

  @Getter
  @Setter
  private ItemStack potion;

  @Getter
  private List<Rank> ranks = new ArrayList<>();

  @Getter
  private Rank rank;

  @Getter
  @Setter
  private Integer shots = 0;

  @Getter
  @Setter
  private boolean spectator = false;
  private final Map<StatsStorage.StatisticType, Integer> stats = new EnumMap<>(StatsStorage.StatisticType.class);

  @Getter
  @Setter
  private DonatType type;

  private final Map<String, Double> cooldowns = new HashMap<>();

  public User(Player player) {
    this.player = player;
  }

  public static void cooldownHandlerTask() {
    Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> cooldownCounter++, 20, 20);
  }

  public Arena getArena() {
    return ArenaRegistry.getArena(player);
  }


  public int getStat(StatsStorage.StatisticType stat) {
    if (!stats.containsKey(stat)) {
      stats.put(stat, 0);
      return 0;
    } else if (stats.get(stat) == null) {
      return 0;
    }
    return stats.get(stat);
  }

  public void removeScoreboard() {
    player.setScoreboard(scoreboardManager.getNewScoreboard());
  }

  public void setStat(StatsStorage.StatisticType stat, int i) {
    stats.put(stat, i);

    //statistics manipulation events are called async when using mysql
    Bukkit.getScheduler().runTask(plugin, () -> {
      MMPlayerStatisticChangeEvent playerStatisticChangeEvent = new MMPlayerStatisticChangeEvent(getArena(), player, stat, i);
      Bukkit.getPluginManager().callEvent(playerStatisticChangeEvent);
    });
  }

  public void addStat(StatsStorage.StatisticType stat, int i) {
    stats.put(stat, getStat(stat) + i);

    //statistics manipulation events are called async when using mysql
    Bukkit.getScheduler().runTask(plugin, () -> {
      MMPlayerStatisticChangeEvent playerStatisticChangeEvent = new MMPlayerStatisticChangeEvent(getArena(), player, stat, getStat(stat));
      Bukkit.getPluginManager().callEvent(playerStatisticChangeEvent);
    });
  }

  public void setCooldown(String s, double seconds) {
    cooldowns.put(s, seconds + cooldownCounter);
  }

  public double getCooldown(String s) {
    if (!cooldowns.containsKey(s) || cooldowns.get(s) <= cooldownCounter) {
      return 0;
    }
    return cooldowns.get(s) - cooldownCounter;
  }

  public void sendMessage(String message) {
    message = ChatColor.translateAlternateColorCodes('&', message);
    player.sendMessage(message);
  }

  public void loadRank() {
    ranks = RankManager.getRanks().stream().filter(r -> this.getStat(StatsStorage.StatisticType.HIGHEST_SCORE) >= r.getXp()).collect(Collectors.toList());
    rank = ranks.get(ranks.size() - 1);
  }
}