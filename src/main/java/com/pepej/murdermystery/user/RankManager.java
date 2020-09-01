package com.pepej.murdermystery.user;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


@UtilityClass
public class RankManager {

    @Getter
    private final List<Rank> ranks = new ArrayList<>();

    public void setupRanks() {
        FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(MurderMystery.class), "ranks");
        for (String key : config.getConfigurationSection("ranks").getKeys(false)) {
            if (key != null) {
                RankManager.getRanks().add(
                        new Rank(
                                config.getString("ranks." + key + ".name"),
                                config.getInt("ranks." + key + ".xp")));
            }
        }
    }
}
