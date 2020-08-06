package pl.plajer.murdermystery.handlers.gui;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import java.util.function.BiConsumer;

@FieldDefaults(level= AccessLevel.PRIVATE)
@AllArgsConstructor
public class Confirmation implements InventoryHolder {

    /**
     * @author pepej
     * Created 15.06.2020
     */

    final Plugin plugin;

    Inventory inventory;

    @Getter String mainDesc;

    public Confirmation(Plugin plugin, String desc) {
        this.plugin = plugin;
        mainDesc = desc;
    }

    public Confirmation build() {
        inventory = Bukkit.createInventory(this, 9, "Вы уверены?");
        fill();
        if (!hasRegisteredListeners) {
            Bukkit.getPluginManager().registerEvents(new ConfirmationListener(), plugin);
            hasRegisteredListeners = true;
        }
        return this;
    }

    public static ItemStack getAcceptItem() {
        return new ItemBuilder(Material.WOOL)
                .data((byte) 13)
                .name("§aПодтвердить")
                .build();
    }

    public static ItemStack getDeclineItem() {
        return new ItemBuilder(Material.WOOL)
                .data((byte) 14)
                .name("§cОтклонить")
                .build();
    }

    @Getter
    BiConsumer<Player, InventoryClickEvent>
            onAccept,
            onDecline,
            onClick,
            onTopClick,
            onBottomClick,
            onOutsideClick;

    @Getter
    private BiConsumer<Player, InventoryCloseEvent> onClose;

    @Getter
    private BiConsumer<Player, InventoryOpenEvent> onOpen;

    private static boolean hasRegisteredListeners;

    /**
     * Get the object's inventory.
     *
     * @return The inventory.
     */
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * @param player - Player to display menu
     * Show this menu for typed Player
     */
    public void show(Player player) {
        player.openInventory(inventory);
    }

    private void fill() {
        this.inventory.setItem(2, getAcceptItem());
        this.inventory.setItem(4, new ItemBuilder(Material.EMPTY_MAP)
                .name("§1")
                .lore(mainDesc)
                .build());
        this.inventory.setItem(6, getDeclineItem());
        for (int i = 0; i < 9; i++) {
            if(i == 2 || i == 4 || i == 6)  continue;
            this.inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).data((byte) 15).name("&1").build());
        }
    }

    public static Inventory getInventory(InventoryView view, int rawSlot) {
        if (rawSlot == InventoryView.OUTSIDE || rawSlot == -1) {
            return null;
        }
        return rawSlot < view.getTopInventory().getSize()
                ? view.getTopInventory()
                : view.getBottomInventory();
    }

    public Confirmation onAccept(BiConsumer<Player, InventoryClickEvent> onAccept) {
        this.onAccept = onAccept;
        return this;
    }

    public Confirmation onDecline(BiConsumer<Player, InventoryClickEvent> onDecline) {
        this.onDecline = onDecline;
        return this;
    }

    public Confirmation onOutsideClick(BiConsumer<Player, InventoryClickEvent> onOutsideClick) {
        this.onOutsideClick = onOutsideClick;
        return this;
    }

    public Confirmation onClose(BiConsumer<Player, InventoryCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }

    public Confirmation onOpen(BiConsumer<Player, InventoryOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    public Confirmation onTopClick(BiConsumer<Player, InventoryClickEvent> onTopClick) {
        this.onTopClick = onTopClick;
        return this;
    }

    public Confirmation onBottomClick(BiConsumer<Player, InventoryClickEvent> onBottomClick) {
        this.onBottomClick = onBottomClick;
        return this;
    }

    public Confirmation onClick(BiConsumer<Player, InventoryClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }
}
