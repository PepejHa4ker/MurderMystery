package pl.plajer.murdermystery.handlers.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.economy.PriceType;
import pl.plajer.murdermystery.perk.PerkRegister;
import pl.plajer.murdermystery.utils.items.ItemBuilder;

import java.util.concurrent.atomic.AtomicInteger;

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
        pane.fillWith(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .name("&c")
                .color((short) 13)
                .build());
        AtomicInteger index = new AtomicInteger(1);
        PerkRegister.getCachedPerks().forEach(perk -> {
            pane.addItem(new GuiItem(new ItemBuilder(perk.getDisplayItem())
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
                    .build(), event -> {
                PriceType type;
                switch (event.getClick()) {
                    case LEFT:
                        type = PriceType.COINS;
                        break;
                    case RIGHT:
                        type = PriceType.KARMA;
                        break;
                    default:
                        return;
                }

                perk.tryBuy((Player) event.getWhoClicked(), type);
                event.getWhoClicked().closeInventory();
            }), index.get(), 1);
            index.incrementAndGet();
        });
        pane.addItem(new GuiItem(new ItemBuilder(Material.EMPTY_MAP)
                .name("&cБаланс: &a" + MurderMystery.getInstance().getEconomy().getBalance(player))
                .build()), 4, 0);
    }

    @Override
    public void show(Player player) {
        gui.show(player);
    }
}
