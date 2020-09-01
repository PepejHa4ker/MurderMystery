package com.pepej.murdermystery.handlers.rewards;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import com.pepej.murdermystery.utils.engine.ScriptEngine;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


public class RewardsFactory {
  private final Set<Reward> rewards = new HashSet<>();
  private final FileConfiguration config;
  private final boolean enabled;

  public RewardsFactory(MurderMystery plugin) {
    enabled = plugin.getConfig().getBoolean("Rewards-Enabled");
    config = ConfigUtils.getConfig(plugin, "rewards");
    registerRewards();
  }

  public void performReward(Arena arena, Reward.RewardType type) {
    if (!enabled) {
      return;
    }
    for (Player p : arena.getPlayers()) {
      performReward(p, type);
    }
  }

  public void performReward(Player player, Reward.RewardType type) {
    if (!enabled) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(player);
    ScriptEngine engine = new ScriptEngine();
    engine.setValue("player", player);
    engine.setValue("server", Bukkit.getServer());
    engine.setValue("arena", arena);
    for (Reward reward : rewards) {
      if (reward.getType() == type) {
        //cannot execute if chance wasn't met
        if (reward.getChance() != -1 && ThreadLocalRandom.current().nextInt(0, 100) > reward.getChance()) {
          continue;
        }
        String command = reward.getExecutableCode();
        command = StringUtils.replace(command, "%PLAYER%", player.getName());
        command = formatCommandPlaceholders(command, arena);
        switch (reward.getExecutor()) {
          case CONSOLE:
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            break;
          case PLAYER:
            player.performCommand(command);
            break;
          case SCRIPT:
            engine.execute(command);
            break;
          default:
            break;
        }
      }
    }
  }

  private String formatCommandPlaceholders(String command, Arena arena) {
    String formatted = command;
    formatted = StringUtils.replace(formatted, "%ARENA-ID%", arena.getId());
    formatted = StringUtils.replace(formatted, "%MAPNAME%", arena.getMapName());
    formatted = StringUtils.replace(formatted, "%PLAYERAMOUNT%", String.valueOf(arena.getPlayers().size()));
    return formatted;
  }

  private void registerRewards() {
    if (!enabled) {
      return;
    }

    Map<Reward.RewardType, Integer> registeredRewards = new HashMap<>();
    for (Reward.RewardType rewardType : Reward.RewardType.values()) {
      for (String reward : config.getStringList("rewards." + rewardType.getPath())) {
        rewards.add(new Reward(rewardType, reward));
        registeredRewards.put(rewardType, registeredRewards.getOrDefault(rewardType, 0) + 1);
      }
    }

  }

}
