/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.murdermystery.handlers.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.special.SpecialBlock;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.setup.SetupInventory;
import pl.plajer.murdermystery.utils.Debugger;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;

/**
 * @author Plajer
 * <p>
 * Created at 25.05.2019
 */
public class SpecialBlocksComponents implements SetupComponent {

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
    Main plugin = setupInventory.getPlugin();

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
      Debugger.debug(Level.INFO, "" + e.getWhoClicked().getTargetBlock(null, 10).getType() + e.getWhoClicked().getTargetBlock(null, 10).getLocation());
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
      ConfigUtils.saveConfig(plugin, config, "arenas");
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
      Debugger.debug(Level.INFO, "" + e.getWhoClicked().getTargetBlock(null, 10).getType() + e.getWhoClicked().getTargetBlock(null, 10).getLocation());
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
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 2, 3);
  }

}
