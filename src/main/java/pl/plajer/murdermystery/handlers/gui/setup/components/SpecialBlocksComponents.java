package pl.plajer.murdermystery.handlers.gui.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.special.SpecialBlock;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.gui.setup.SetupInventory;
import pl.plajer.murdermystery.utils.compat.XMaterial;
import pl.plajer.murdermystery.utils.config.ConfigUtils;
import pl.plajer.murdermystery.utils.items.ItemBuilder;
import pl.plajer.murdermystery.utils.serialization.LocationSerializer;

import java.util.ArrayList;
import java.util.List;


public class SpecialBlocksComponents implements ArenaSetupGuiComponent {

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

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.PAPER.parseItem())
      .name(ChatManager.colorRawMessage("&6&lВыбор специальных блоков"))
      .lore(ChatColor.GRAY + "Предметы справа позволят")
      .lore(ChatColor.GRAY + "Вам добавить специальные игровые блоки!")
      .build()), 0, 3);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.ENDER_CHEST.parseItem())
      .name(ChatManager.colorRawMessage("&e&lДобавить Таинственный Котел"))
      .lore(ChatColor.GRAY + "Нацельтесь на котёл и добавьте его в игру")
      .lore(ChatColor.GRAY + "это будет стоить 1 золото за зелье!")
      .lore(ChatColor.GRAY + "Настроить котельные зелья в specialblocks.yml файле!")
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      if (e.getWhoClicked().getTargetBlock(null, 15).getType() != XMaterial.CAULDRON.parseMaterial()) {
        e.getWhoClicked().sendMessage(ChatColor.RED + "Пожалуйста, нацельтесь на котёл для продолжения!");
        return;
      }

      arena.loadSpecialBlock(new SpecialBlock(e.getWhoClicked().getTargetBlock(null, 10).getLocation(),
        SpecialBlock.SpecialBlockType.MYSTERY_CAULDRON));
      List<String> cauldrons = new ArrayList<>(config.getStringList("instances." + arena.getId() + ".mystery-cauldrons"));
      cauldrons.add(LocationSerializer.locationToString(e.getWhoClicked().getTargetBlock(null, 10).getLocation()));
      config.set("instances." + arena.getId() + ".mystery-cauldrons", cauldrons);
      player.sendMessage(ChatManager.colorRawMessage("&e✔ Завершено | &aДобавлен Котёльный специальный блок!"));
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
    }), 1, 3);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.ENCHANTING_TABLE.parseItem())
      .name(ChatManager.colorRawMessage("&e&lДобавить стол молитвы"))
      .lore(ChatColor.GRAY + "Нацельтесь на стол зачарования и")
      .lore(ChatColor.GRAY + "добавьте цену для разработчика")
      .lore(ChatColor.GRAY + "исповедальня, подарок для")
      .lore(ChatColor.GRAY + "разработчик стоит 1 золото!")
      .lore(ChatColor.GOLD + "Добавьте несколько рычагов в радиусе")
      .lore(ChatColor.GOLD + "3х блоков рядом со столом зачарования")
      .lore(ChatColor.GOLD + "разрешить пользователям там помолиться!")
      .lore(ChatColor.RED + "Вы можете получить подарки")
      .lore(ChatColor.RED + "или проклятия от молитвы!")
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      if (e.getWhoClicked().getTargetBlock(null, 15).getType() != XMaterial.ENCHANTING_TABLE.parseMaterial()) {
        e.getWhoClicked().sendMessage(ChatColor.RED + "Пожалуйста нацельтесь на стол зачарования для продолжения!");
        return;
      }

      arena.loadSpecialBlock(new SpecialBlock(e.getWhoClicked().getTargetBlock(null, 10).getLocation(),
        SpecialBlock.SpecialBlockType.PRAISE_DEVELOPER));
      List<String> confessionals = new ArrayList<>(config.getStringList("instances." + arena.getId() + ".confessionals"));
      confessionals.add(LocationSerializer.locationToString(e.getWhoClicked().getTargetBlock(null, 10).getLocation()));
      config.set("instances." + arena.getId() + ".confessionals", confessionals);
      player.sendMessage(ChatManager.colorRawMessage("&e✔ Завершено | &aДобавлен специальный молитвенный блок!"));
      player.sendMessage(ChatManager.colorRawMessage("&eInfo | &aНе забудьте поместить любой рычаг в радиусе 3 рядом с чародейным столом!"));
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
    }), 2, 3);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.ANVIL.parseItem())
            .name(ChatManager.colorRawMessage("&e&lДобавить волшебную кузницу"))
            .build(), e -> {
      e.getWhoClicked().closeInventory();
      if (e.getWhoClicked().getTargetBlock(null, 15).getType() != XMaterial.ANVIL.parseMaterial()) {
        e.getWhoClicked().sendMessage(ChatColor.RED + "Пожалуйста нацельтесь на наковальню для продолжения!");
        return;
      }


      arena.loadSpecialBlock(new SpecialBlock(e.getWhoClicked().getTargetBlock(null, 10).getLocation(),
              SpecialBlock.SpecialBlockType.MAGIC_ANVIL));
      List<String> anvils = new ArrayList<>(config.getStringList("instances." + arena.getId() + ".magicanvils"));
      anvils.add(LocationSerializer.locationToString(e.getWhoClicked().getTargetBlock(null, 10).getLocation()));
      config.set("instances." + arena.getId() + ".magicanvils", anvils);
      player.sendMessage(ChatManager.colorRawMessage("&e✔ Завершено | &aДобавлен специальный блок кузницы!"));
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
    }), 3, 3);
  }

  @Override
  public void show(Player player) {

  }

}
