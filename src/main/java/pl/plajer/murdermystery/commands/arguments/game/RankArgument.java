package pl.plajer.murdermystery.commands.arguments.game;

import lombok.val;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.commands.arguments.ArgumentsRegistry;
import pl.plajer.murdermystery.commands.arguments.data.CommandArgument;
import pl.plajer.murdermystery.user.User;
import pl.plajer.murdermystery.utils.config.ConfigUtils;

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
