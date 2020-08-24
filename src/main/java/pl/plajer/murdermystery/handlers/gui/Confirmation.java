package pl.plajer.murdermystery.handlers.gui;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
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
import org.jetbrains.annotations.NotNull;
import pl.plajer.murdermystery.utils.items.ItemBuilder;

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


    @Getter
    final String mainDesc;

    public Confirmation(Plugin plugin, String desc) {
        this.plugin = plugin;
        mainDesc = desc;
    }

    public Confirmation build() {
        inventory = Bukkit.createInventory(this, 9, "Вы уверены?");
        this.fill();
        if (!hasRegisteredListeners) {
            Bukkit.getPluginManager().registerEvents(new ConfirmationListener(), plugin);
            hasRegisteredListeners = true;
        }
        return this;
    }

    public static ItemStack getAcceptItem() {
        return new ItemBuilder(Material.WOOL)
                .color((short) 13)
                .name("&aПодтвердить")
                .build();
    }

    public static ItemStack getDeclineItem() {
        return new ItemBuilder(Material.WOOL)
                .color((short) 14)
                .name("&cОтклонить")
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
            this.inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).color((short) 13).name("&1").build());
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

    public Confirmation onAccept(@NotNull BiConsumer<Player, InventoryClickEvent> onAccept) {
        this.onAccept = onAccept;
        return this;
    }

    public Confirmation onDecline(@NotNull BiConsumer<Player, InventoryClickEvent> onDecline) {
        this.onDecline = onDecline;
        return this;
    }

    public Confirmation onOutsideClick(@NotNull BiConsumer<Player, InventoryClickEvent> onOutsideClick) {
        this.onOutsideClick = onOutsideClick;
        return this;
    }

    public Confirmation onClose(@NotNull BiConsumer<Player, InventoryCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }

    public Confirmation onOpen(@NotNull BiConsumer<Player, InventoryOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    public Confirmation onTopClick(@NotNull BiConsumer<Player, InventoryClickEvent> onTopClick) {
        this.onTopClick = onTopClick;
        return this;
    }

    public Confirmation onBottomClick(@NotNull BiConsumer<Player, InventoryClickEvent> onBottomClick) {
        this.onBottomClick = onBottomClick;
        return this;
    }

    public Confirmation onClick(@NotNull BiConsumer<Player, InventoryClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }
}
