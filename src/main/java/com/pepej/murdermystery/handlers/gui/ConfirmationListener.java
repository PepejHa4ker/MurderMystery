package com.pepej.murdermystery.handlers.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.function.Consumer;

public class ConfirmationListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Confirmation)) {
            return;
        }

        Confirmation confirmation = (Confirmation) event.getInventory().getHolder();

        Consumer<InventoryClickEvent> onOutsideClick = confirmation.getOnOutsideClick();

        if (event.getClickedInventory() == null) {
            onOutsideClick.accept(event);
            return;
        }

        Consumer<InventoryClickEvent> onAccept = confirmation.getOnAccept();
        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(Confirmation.getAcceptItem())) {
            onAccept.accept(event);
            return;
        }
        Consumer<InventoryClickEvent> onDecline = confirmation.getOnDecline();

        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(Confirmation.getDeclineItem()) ) {
            onDecline.accept(event);
            return;
        }

        Consumer<InventoryClickEvent> onClick = confirmation.getOnClick();
        if (onClick != null) {
            event.setCancelled(true);
            onClick.accept(event);
        }

        InventoryView view = event.getView();
        Inventory inventory = Confirmation.getInventory(view, event.getRawSlot());
        if (inventory == null) {
            return;
        }

        Consumer<InventoryClickEvent> onTopClick = confirmation.getOnTopClick();
        if (inventory.equals(view.getTopInventory())) {
            onTopClick.accept(event);
        }

        Consumer<InventoryClickEvent> onBottomClick = confirmation.getOnBottomClick();
        if (inventory.equals(view.getBottomInventory())) {
            onBottomClick.accept(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof Confirmation)) {
            return;
        }
        Confirmation confirmation = (Confirmation) event.getInventory().getHolder();
        Consumer<InventoryOpenEvent> onOpen = confirmation.getOnOpen();
        if (onOpen != null) {
            onOpen.accept(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Confirmation)) {
            return;
        }
        Confirmation confirmation = (Confirmation) event.getInventory().getHolder();
        Consumer<InventoryCloseEvent> onClose = confirmation.getOnClose();
        if (onClose != null) {
            onClose.accept(event);
        }

    }
}
