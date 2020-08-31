
package pl.plajer.murdermystery.handlers.gui.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.gui.setup.SetupInventory;
import pl.plajer.murdermystery.utils.config.ConfigUtils;
import pl.plajer.murdermystery.utils.items.ItemBuilder;
import pl.plajer.murdermystery.utils.serialization.LocationSerializer;

import java.util.ArrayList;
import java.util.List;
public class SpawnComponents implements ArenaSetupGuiComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    Player player = setupInventory.getPlayer();
    FileConfiguration config = setupInventory.getConfig();
    Arena arena = setupInventory.getArena();
    String serializedLocation = player.getLocation().getWorld().getName() + "," + player.getLocation().getX() + "," + player.getLocation().getY() + ","
      + player.getLocation().getZ() + "," + player.getLocation().getYaw() + ",0.0";
    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE_BLOCK)
      .name(ChatManager.colorRawMessage("&e&lУстановить локацию окончания"))
      .lore(ChatColor.GRAY + "Нажмите, чтобы установить конечную локацию")
      .lore(ChatColor.GRAY + "на месте, где Вы стоите")
      .lore(ChatColor.DARK_GRAY + "(локация, куда игроки будут")
      .lore(ChatColor.DARK_GRAY + "телепортированны после игры)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".Endlocation"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".Endlocation", serializedLocation);
      arena.setEndLocation(player.getLocation());
      player.sendMessage(ChatManager.colorRawMessage("&e✔ Завершено | &aЛокация окончания для арены " + arena.getId() + " установлена на Вашем месте!"));
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
    }), 0, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.LAPIS_BLOCK)
      .name(ChatManager.colorRawMessage("&e&lУстановить локацию лобби"))
      .lore(ChatColor.GRAY + "Нажмите, чтобы установить локацию лобби")
      .lore(ChatColor.GRAY + "на месте, где Вы стоите")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneBool("instances." + arena.getId() + ".lobbylocation"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      config.set("instances." + arena.getId() + ".lobbylocation", serializedLocation);
      arena.setLobbyLocation(player.getLocation());
      player.sendMessage(ChatManager.colorRawMessage("&e✔ Завершено | &aЛокация лобби для арены " + arena.getId() + " установлена на Вашем месте!"));
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
    }), 1, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.EMERALD_BLOCK)
      .name(ChatManager.colorRawMessage("&e&lДобавить локацию старта"))
      .lore(ChatColor.GRAY + "Нажмите, чтобы установить стартовую локацию")
      .lore(ChatColor.GRAY + "на месте, где вы стоите.")
      .lore(ChatColor.DARK_GRAY + "(локация, куда игроки будут")
      .lore(ChatColor.DARK_GRAY + "телепортированны, когда игра начнётся)")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneList("instances." + arena.getId() + ".playerspawnpoints", 4))
      .lore("", ChatManager.colorRawMessage("&8Shift + Правый клик чтобы удалить все спавны"))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      if (e.getClick() == ClickType.SHIFT_RIGHT) {
        config.set("instances." + arena.getId() + ".playerspawnpoints", new ArrayList<>());
        arena.setPlayerSpawnPoints(new ArrayList<>());
        player.sendMessage(ChatManager.colorRawMessage("&eГотово | &aТочки спавна игроков удалены, Вы можете добавить их снова!"));
        arena.setReady(false);
        ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
        return;
      }
      List<String> startingSpawns = config.getStringList("instances." + arena.getId() + ".playerspawnpoints");
      startingSpawns.add(LocationSerializer.locationToString(player.getLocation()));
      config.set("instances." + arena.getId() + ".playerspawnpoints", startingSpawns);
      String startingProgress = startingSpawns.size() >= 4 ? "&e✔ Завершено | " : "&c✘ Не завершено | ";
      player.sendMessage(ChatManager.colorRawMessage(startingProgress + "&aСпавн игрокой добавлен! &8(&7" + startingSpawns.size() + "/4&8)"));
      if (startingSpawns.size() == 4) {
        player.sendMessage(ChatManager.colorRawMessage("&eInfo | &aВы можете добавить больше 4 спавнов! 4 просто минимум!"));
      }
      List<Location> spawns = new ArrayList<>(arena.getPlayerSpawnPoints());
      spawns.add(player.getLocation());
      arena.setPlayerSpawnPoints(spawns);
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
    }), 2, 0);
  }

  @Override
  public void show(Player player) {

  }

}
