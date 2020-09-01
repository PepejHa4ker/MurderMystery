package com.pepej.murdermystery.handlers.gui.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.handlers.gui.setup.SetupInventory;
import com.pepej.murdermystery.utils.compat.XMaterial;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import com.pepej.murdermystery.utils.items.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;


public class PlayerAmountComponents implements ArenaSetupGuiComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    FileConfiguration config = setupInventory.getConfig();
    Arena arena = setupInventory.getArena();
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
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
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
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
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
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
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
      ConfigUtils.saveConfig(MurderMystery.getInstance(), config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 4, 1);
  }

  @Override
  public void show(Player player) {

  }

}
