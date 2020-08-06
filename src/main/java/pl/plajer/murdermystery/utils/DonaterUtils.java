package pl.plajer.murdermystery.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.murdermystery.Main;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

@UtilityClass
public class DonaterUtils {

    private Main plugin = JavaPlugin.getPlugin(Main.class);
    private FileConfiguration donatConfig = ConfigUtils.getConfig(plugin, "donaters");
    public float getMultiplier(DonatType type) {
        return (float) donatConfig.getDouble(type.name().toLowerCase() +".multiplier");

    }
}




