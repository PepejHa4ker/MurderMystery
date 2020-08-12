package pl.plajer.murdermystery.utils.donate;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.utils.config.ConfigUtils;

@UtilityClass
public class DonaterUtils {

    private final MurderMystery plugin = JavaPlugin.getPlugin(MurderMystery.class);
    private final FileConfiguration donatConfig = ConfigUtils.getConfig(plugin, "donaters");

    public float getMultiplier(DonatType type) {
        return (float) donatConfig.getDouble(type.name().toLowerCase() +".multiplier");

    }
}




