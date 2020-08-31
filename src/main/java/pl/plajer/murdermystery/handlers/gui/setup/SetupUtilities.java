
package pl.plajer.murdermystery.handlers.gui.setup;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.utils.serialization.LocationSerializer;


@AllArgsConstructor
public class SetupUtilities {

  private final FileConfiguration config;
  private final Arena arena;


  public String isOptionDone(String path) {
    if (config.isSet(path)) {
      return ChatManager.colorRawMessage("&a&l✔ Завершено &7(значение: &8" + config.getString(path) + "&7)");
    }
    return ChatManager.colorRawMessage("&c&l✘ Не завершено");
  }

  public String isOptionDoneList(String path, int minimum) {
    if (config.isSet(path)) {
      if (config.getStringList(path).size() < minimum) {
        return ChatManager.colorRawMessage("&c&l✘ Не завершено | &cПожалуйста, добавьте больше спавнов");
      }
      return ChatManager.colorRawMessage("&a&l✔ Завершено &7(значение: &8" + config.getStringList(path).size() + "&7)");
    }
    return ChatManager.colorRawMessage("&c&l✘ Не завершено");
  }

  public String isOptionDoneBool(String path) {
    if (config.isSet(path)) {
      if (Bukkit.getServer().getWorlds().get(0).getSpawnLocation().equals(LocationSerializer.getLocation(config.getString(path)))) {
        return ChatManager.colorRawMessage("&c&l✘ Не завершено");
      }
      return ChatManager.colorRawMessage("&a&l✔ Завершено");
    }
    return ChatManager.colorRawMessage("&c&l✘ Не завершено");
  }

  public int getMinimumValueHigherThanZero(String path) {
    int amount = config.getInt("instances." + arena.getId() + "." + path);
    if (amount == 0) {
      amount = 1;
    }
    return amount;
  }

}
