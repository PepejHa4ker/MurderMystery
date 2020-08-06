package pl.plajer.murdermystery.handlers.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.val;
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

    public PerkGui() {
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
        AtomicInteger index = new AtomicInteger(3);
        Perk.getAllPerks().forEach(perk -> {
            pane.addItem(new GuiItem(perk.getDisplayItem(), event -> {
                val user = plugin.getUserManager().getUser((Player) event.getWhoClicked());
                if (user.getPerks().contains(perk)) {
                    user.getPlayer().sendMessage("§cПерк уже выбран");
                    return;
                }
                if (plugin.getEconomy().getBalance(user.getPlayer()) < perk.getPrice()) {
                    user.getPlayer().sendMessage("§cНедостаточно средств.");
                    return;
                }

                if (user.getPerks().size() == 1) {
                    user.getPlayer().sendMessage("§cВы можете взять только 1 способность на игру.");
                    return;
                }

                plugin.getEconomy().withdrawPlayer(user.getPlayer(), perk.getPrice());
                user.getPerks().add(perk);
                event.getWhoClicked().sendMessage("§6Перк " + perk.getName() + " §6успешно выбран.");
                event.getWhoClicked().closeInventory();
            }), index.get(), 1);
            index.incrementAndGet();
        });
    }

    @Override
    public void show(Player player) {
        gui.show(player);
    }
}
