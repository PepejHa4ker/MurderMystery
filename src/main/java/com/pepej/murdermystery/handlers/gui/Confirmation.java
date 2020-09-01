package com.pepej.murdermystery.handlers.gui;

import com.pepej.murdermystery.utils.items.ItemBuilder;
import lombok.Getter;
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

import java.util.function.Consumer;

@Getter
public class Confirmation implements InventoryHolder {

    private final Plugin plugin;
    private final String description;

    private Inventory inventory;


    public Confirmation(Plugin plugin, String description) {
        this.plugin = plugin;
        this.description = description;
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
                .color(13)
                .name("&aПодтвердить")
                .build();
    }

    public static ItemStack getDeclineItem() {
        return new ItemBuilder(Material.WOOL)
                .color(14)
                .name("&cОтклонить")
                .build();
    }

    Consumer<InventoryClickEvent>
            onAccept,
            onDecline,
            onClick,
            onTopClick,
            onBottomClick,
            onOutsideClick;

    private Consumer<InventoryCloseEvent> onClose;

    private Consumer<InventoryOpenEvent> onOpen;


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
                .lore(this.description)
                .build());
        this.inventory.setItem(6, getDeclineItem());
        for (int i = 0; i < 9; i++) {
            if (i == 2 || i == 4 || i == 6) continue;
            this.inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).color(13).name("&1").build());
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

    /**
     * onAccept callback
     *
     * @param onAccept - Called every time the player accepts the conditions
     * @return {@link Confirmation}
     */

    public Confirmation onAccept(@NotNull Consumer<InventoryClickEvent> onAccept) {
        this.onAccept = onAccept;
        return this;
    }

    /**
     * onDecline callback
     *
     * @param onDecline - Called every time the player decline the conditions
     * @return {@link Confirmation}
     */

    public Confirmation onDecline(@NotNull Consumer<InventoryClickEvent> onDecline) {
        this.onDecline = onDecline;
        return this;
    }

    /**
     * onOutsideClick callback
     *
     * @param onOutsideClick - Called every time the player click on outside space
     * @return {@link Confirmation}
     */

    public Confirmation onOutsideClick(@NotNull Consumer<InventoryClickEvent> onOutsideClick) {
        this.onOutsideClick = onOutsideClick;
        return this;
    }

    /**
     * onClose callback
     *
     * @param onClose - Called every time the player close the inventory
     * @return {@link Confirmation}
     */

    public Confirmation onClose(@NotNull Consumer<InventoryCloseEvent> onClose) {
        this.onClose = onClose;
        return this;
    }

    /**
     * onOpen callback
     *
     * @param onOpen - Called every time the player open the inventory
     * @return {@link Confirmation}
     */

    public Confirmation onOpen(@NotNull Consumer<InventoryOpenEvent> onOpen) {
        this.onOpen = onOpen;
        return this;
    }

    /**
     * onTopClick callback
     *
     * @param onTopClick - Called every time the player click on top inventory
     * @return {@link Confirmation}
     */

    public Confirmation onTopClick(@NotNull Consumer<InventoryClickEvent> onTopClick) {
        this.onTopClick = onTopClick;
        return this;
    }

    /**
     * onBottomClick callback
     *
     * @param onBottomClick - Called every time the player click on bottom inventory
     * @return {@link Confirmation}
     */

    public Confirmation onBottomClick(@NotNull Consumer<InventoryClickEvent> onBottomClick) {
        this.onBottomClick = onBottomClick;
        return this;
    }

    /**
     * Click callback
     *
     * @param onClick - Called every time the player click inventory
     * @return {@link Confirmation}
     */

    public Confirmation onClick(@NotNull Consumer<InventoryClickEvent> onClick) {
        this.onClick = onClick;
        return this;
    }
}
