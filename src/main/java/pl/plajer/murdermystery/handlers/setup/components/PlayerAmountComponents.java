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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.setup.SetupInventory;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 25.05.2019
 */
public class PlayerAmountComponents implements SetupComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    FileConfiguration config = setupInventory.getConfig();
    Arena arena = setupInventory.getArena();
    Main plugin = setupInventory.getPlugin();
    pane.addItem(new GuiItem(new ItemBuilder(Material.COAL).amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("minimumplayers"))
      .name(ChatManager.colorRawMessage("&e&lУстановить минимальное количество игроков"))
      .lore(ChatColor.GRAY + "ЛЕВЫЙ клик, чтобы уменшить")
      .lore(ChatColor.GRAY + "ПРАВЫЙ клик, чтобы увеличить")
      .lore(ChatColor.DARK_GRAY + "(как много игроков нужно")
      .lore(ChatColor.DARK_GRAY + "для игры, чтобы начать отсчёт до старта)")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".minimumplayers"))
      .build(), e -> {
      if (e.getClick().isRightClick()) {
        e.getInventory().getItem(e.getSlot()).setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        e.getInventory().getItem(e.getSlot()).setAmount(e.getCurrentItem().getAmount() - 1);
      }
      if (e.getInventory().getItem(e.getSlot()).getAmount() <= 1) {
        e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&c&l✖ &cПредупреждение | Пожалуйста, не устанавливайте количество меньше чем 2! Игра разработана для 2 или больше игроков!"));
        e.getInventory().getItem(e.getSlot()).setAmount(2);
      }
      config.set("instances." + arena.getId() + ".minimumplayers", e.getCurrentItem().getAmount());
      arena.setMinimumPlayers(e.getCurrentItem().getAmount());
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 3, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.REDSTONE)
      .amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("maximumplayers"))
      .name(ChatManager.colorRawMessage("&e&lУстановить максимальное количество игроков"))
      .lore(ChatColor.GRAY + "ЛЕВЫЙ клик, чтобы уменьшить")
      .lore(ChatColor.GRAY + "ПРАВЫЙ клик, чтобы увеличить")
      .lore(ChatColor.DARK_GRAY + "(как много игроков арена может содержать)")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".maximumplayers"))
      .build(), e -> {
      if (e.getClick().isRightClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
      }
      if (e.getInventory().getItem(e.getSlot()).getAmount() <= 1) {
        e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&c&l✖ &cПредупреждение | Пожалуйста, не устанавливайте количество меньше чем 2! Игра разработана для 2 или больше игроков!"));
        e.getInventory().getItem(e.getSlot()).setAmount(2);
      }
      config.set("instances." + arena.getId() + ".maximumplayers", e.getCurrentItem().getAmount());
      arena.setMaximumPlayers(e.getCurrentItem().getAmount());
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 4, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.IRON_SWORD.parseItem())
      .amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("playerpermurderer"))
      .name(ChatManager.colorRawMessage("&e&lУстановить количество маньяков"))
      .lore(ChatColor.GRAY + "ЛЕВЫЙ клик, чтобы уменьшить")
      .lore(ChatColor.GRAY + "ПРАВЫЙ клик, чтобы увеличить")
      .lore(ChatColor.DARK_GRAY + "Как много маньяков должно быть в игре? Это значит ")
      .lore(ChatColor.DARK_GRAY + "один маньяк за такое количество игроков. Default: ")
      .lore(ChatColor.DARK_GRAY + "5 игроков - 1 убийца, это означает, что если мы имеем ")
      .lore(ChatColor.DARK_GRAY + "из 14 игроков высчитает 2 маньяка! ")
      .lore(ChatColor.DARK_GRAY + "Установите это на 1, если вы хотите только 1 маньяка ")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".playerpermurderer"))
      .build(), e -> {
      if (e.getClick().isRightClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        if (e.getCurrentItem().getAmount() > 1) {
          e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
        }
      }
      if (e.getInventory().getItem(e.getSlot()).getAmount() < 1) {
        e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&c&l✖ &cПредупреждение | Пожалуйста, не ставьте количество меньше чем 1! Игра разработана для большего кол-ва маньяков, чем игрооков."));
        e.getInventory().getItem(e.getSlot()).setAmount(1);
      }
      config.set("instances." + arena.getId() + ".playerpermurderer", e.getCurrentItem().getAmount());
      arena.setMurderers(e.getCurrentItem().getAmount());
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 3, 1);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.BOW.parseItem())
      .amount(setupInventory.getSetupUtilities().getMinimumValueHigherThanZero("playerperdetective"))
      .name(ChatManager.colorRawMessage("&e&lУстановить количество игроков на роль детектива"))
      .lore(ChatColor.GRAY + "ЛЕВЫЙ клик, чтобы уменьшить")
      .lore(ChatColor.GRAY + "ПРАВЫЙ клик, чтобы увеличить")
      .lore(ChatColor.DARK_GRAY + "Как много детективов должно быть в игре? Это значит ")
      .lore(ChatColor.DARK_GRAY + "один детектив на такое количество игроков. По умолчанию: ")
      .lore(ChatColor.DARK_GRAY + "7 игроков 1 детектив, это значит, что если у нас есть ")
      .lore(ChatColor.DARK_GRAY + "18 Игроков это высчитает 2 детектива! ")
      .lore(ChatColor.DARK_GRAY + "Установите это на 1, если вы хотите только одного детектива! ")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".playerperdetective"))
      .build(), e -> {
      if (e.getClick().isRightClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        if (e.getCurrentItem().getAmount() > 1) {
          e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
        }
      }
      if (e.getInventory().getItem(e.getSlot()).getAmount() < 1) {
        e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&c&l✖ &cПредупреждение | Пожалуйста, не ставьте значение меньше чем 1, Игра не рассчитана на большее количество детективов, чем игроков"));
        e.getInventory().getItem(e.getSlot()).setAmount(1);
      }
      config.set("instances." + arena.getId() + ".playerperdetective", e.getCurrentItem().getAmount());
      arena.setDetectives(e.getCurrentItem().getAmount());
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 4, 1);
  }

}
