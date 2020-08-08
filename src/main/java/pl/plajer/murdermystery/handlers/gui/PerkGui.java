package pl.plajer.murdermystery.handlers.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.perks.Perk;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import java.util.concurrent.atomic.AtomicInteger;

public class PerkGui implements GuiComponent {

    private static Main plugin = JavaPlugin.getPlugin(Main.class);
    private Gui gui;
    private Player player;

    public PerkGui(Player player) {
        this.player = player;
        this.gui = new Gui(plugin, 3, "Выберите способность");
        StaticPane pane = new StaticPane(9, 3);
        this.gui.addPane(pane);
        injectComponents(pane);
    }

    @Override
    public void injectComponents(StaticPane pane) {
        pane.setOnClick(event -> event.setCancelled(true));
        pane.fillWith(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .name("§c")
                .data((byte) 14)
                .build());
        AtomicInteger index = new AtomicInteger(2);
        Perk.getAllPerks().forEach(perk -> {
            pane.addItem(new GuiItem(perk.getDisplayItem(), event -> {
                perk.buy((Player) event.getWhoClicked());
                event.getWhoClicked().closeInventory();
            }), index.get(), 1);
            index.incrementAndGet();
        });
        pane.addItem(new GuiItem(new ItemBuilder(Material.EMPTY_MAP)
                .name("§cБаланс: §a" + plugin.getEconomy().getBalance(player))
                .build()), 4, 0);
    }

    @Override
    public void show(Player player) {
        gui.show(player);
    }
}
