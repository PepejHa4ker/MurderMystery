package pl.plajer.murdermystery.user;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.bukkit.configuration.file.FileConfiguration;


@UtilityClass
public class RankManager {

    public void setupRanks() {
        FileConfiguration config = Rank.getRankConfig();
        for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
            if (key != null) {
                val rank = new Rank(
                        config.getString("ranks." + key + ".name"),
                        config.getInt("ranks." + key + ".xp"));
                Rank.getRanks().add(rank);
            }
        }
    }
}
