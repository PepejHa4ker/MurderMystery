package com.pepej.murdermystery.utils.donate;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@UtilityClass
public class DonaterUtils {

    private final MurderMystery plugin = JavaPlugin.getPlugin(MurderMystery.class);
    private final FileConfiguration donatConfig = ConfigUtils.getConfig(plugin, "donaters");

    public float getMultiplier(DonatType type) {
        return (float) donatConfig.getDouble(type.name().toLowerCase() +".multiplier");

    }
}




