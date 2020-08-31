
package pl.plajer.murdermystery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import pl.plajer.murdermystery.utils.compat.XMaterial;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Plajer
 * <p>
 * Created at 22.12.2018
 */
public class ConfigPreferences {

  private final MurderMystery plugin;
  private ItemStack murdererSword;
  private final Map<Option, Boolean> options = new HashMap<>();

  public ConfigPreferences(MurderMystery plugin) {
    this.plugin = plugin;
    loadOptions();
    loadMurdererSword();
  }

  private void loadMurdererSword() {
    try {
      murdererSword = XMaterial.fromString(plugin.getConfig().getString("Murderer-Sword-Material", "IRON_SWORD").toUpperCase()).parseItem();
    } catch (Exception ex) {
      MurderMystery.getInstance().getPluginLogger().severe("Cannot find murder sword!");
      murdererSword = XMaterial.IRON_SWORD.parseItem();
    }
  }

  /**
   * Returns whether option value is true or false
   *
   * @param option option to get value from
   * @return true or false based on user configuration
   */
  public boolean getOption(Option option) {
    return options.get(option);
  }

  private void loadOptions() {
    for (Option option : Option.values()) {
      options.put(option, plugin.getConfig().getBoolean(option.getPath(), option.getDefault()));
    }
  }

  @AllArgsConstructor
  public enum Option {
    BOSSBAR_ENABLED("Bossbar-Enabled", true), BUNGEE_ENABLED("BungeeActivated", false), CHAT_FORMAT_ENABLED("ChatFormat-Enabled", true),
    DATABASE_ENABLED("DatabaseActivated", false), INVENTORY_MANAGER_ENABLED("InventoryManager", true), NAMETAGS_HIDDEN("Nametags-Hidden", true),
    DISABLE_FALL_DAMAGE("Disable-Fall-Damage", false), ENABLE_SHORT_COMMANDS("Enable-Short-Commands", false);

    @Getter
    private final String path;
    private final boolean def;


    /**
     * @return default value of option if absent in config
     */
    public boolean getDefault() {
      return def;
    }
  }

  public ItemStack getMurdererSword() {
    return murdererSword;
  }
}
