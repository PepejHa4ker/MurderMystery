package com.pepej.murdermystery.handlers.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.perk.PerkRegister;
import com.pepej.murdermystery.user.User;
import com.pepej.murdermystery.utils.Utils;
import com.pepej.murdermystery.utils.items.ItemBuilder;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class PerkGui implements GuiComponent {


    private final Gui gui;
    private final Player player;

    public PerkGui(Player player) {
        this.player = player;
        this.gui = new Gui(MurderMystery.getInstance(), 3, "Выберите способность");
        StaticPane pane = new StaticPane(9, 3);
        this.gui.addPane(pane);
        injectComponents(pane);
    }

    @Override
    public void injectComponents(StaticPane pane) {
        pane.setOnClick(event -> event.setCancelled(true));
        User user = MurderMystery.getInstance().getUserManager().getUser(player);
        pane.fillWith(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .name("&c")
                .color(13)
                .build());
        pane.addItem(new GuiItem(new ItemBuilder(Material.BOOK)
                .name("&eВыбранные способности:")
                .lore(user.getCachedPerks()
                          .stream()
                          .map(perk -> "     " + perk.getName())
                          .collect(Collectors.toList()))
                .build()
        ), 8, 0);
        val perkItems = PerkRegister.getCachedPerks()
                                    .stream()
                                    .map(perk -> new ItemBuilder(perk.getDisplayItem())
                                            .name(perk.getName())
                                            .lore("&7&m---------------------")
                                            .lore(perk.getDescription())
                                            .lore("&7&m---------------------")
                                            .lore("&eЦена: ")
                                            .lore("     &c" + perk.getCost() + "&e монет")
                                            .lore("     &eИЛИ")
                                            .lore("     &c" + (int) (perk.getCost() / 10) + "&e кармы")
                                            .lore("&eЛКМ - &aмонеты")
                                            .lore("&eПКМ - &dкарма")
                                            .build())
                                    .collect(Collectors.toList());

        Utils.perkMatrixRegister(pane, 1, perkItems);
        pane.addItem(new GuiItem(new ItemBuilder(Material.EMPTY_MAP)
                .name("&cБаланс: &a" + MurderMystery.getInstance().getEconomy().getBalance(player))
                .build()), 4, 0);
    }

    @Override
    public void show(Player player) {
        gui.show(player);
    }
}
