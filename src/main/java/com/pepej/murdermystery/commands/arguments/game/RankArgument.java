package com.pepej.murdermystery.commands.arguments.game;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.user.User;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RankArgument {
    public RankArgument(ArgumentsRegistry registry) {
        registry.mapArgument("murdermystery", new CommandArgument("ranks", "", CommandArgument.ExecutorType.PLAYER) {
            @Override
            public void execute(CommandSender sender, String[] args) {
                Player player = (Player) sender;
                FileConfiguration ranks = ConfigUtils.getConfig(JavaPlugin.getPlugin(MurderMystery.class), "ranks");
                User user = registry.getPlugin().getUserManager().getUser((Player) sender);
                player.sendMessage("§6Список рангов");
                player.sendMessage("§c§m--------------------");
                for(String key : ranks.getConfigurationSection("ranks").getKeys(false)) {
                    val rankName = ranks.getString("ranks." + key + ".name").replaceAll("[^А-Яа-я]", "");
                    val xpNeeded = ranks.getInt("ranks." + key + ".xp");
                    if(user.getStat(StatsStorage.StatisticType.HIGHEST_SCORE) >= xpNeeded) {
                        sender.sendMessage("§eРанг: §c" + rankName + " §eОпыта: §c" + xpNeeded + "§6(Получен)");
                    } else {
                        sender.sendMessage("§eРанг: §c" + rankName + " §eОпыта: §c" + xpNeeded);
                    }
                }
                player.sendMessage("§c§m--------------------");
            }
        });
    }
}
