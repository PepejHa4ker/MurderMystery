package pl.plajer.murdermystery.handlers.gui;

import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class ConfirmationListener implements Listener {

    /**
     * @author pepej
     * Created 15.06.2020
     */

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof Confirmation)) {
            return;
        }

        val confirmation = (Confirmation) event.getInventory().getHolder();

        val onOutsideClick = confirmation.getOnOutsideClick();

        if (event.getClickedInventory() == null) {
            onOutsideClick.accept((Player) event.getWhoClicked(), event);
            return;
        }

        val onAccept = confirmation.getOnAccept();
        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(Confirmation.getAcceptItem())) {
            onAccept.accept((Player) event.getWhoClicked(), event);
            event.setCancelled(true);
            return;
        }
        val onDecline = confirmation.getOnDecline();

        if (event.getCurrentItem() != null && event.getCurrentItem().isSimilar(Confirmation.getDeclineItem()) ) {
            onDecline.accept((Player) event.getWhoClicked(), event);
            event.setCancelled(true);
            return;
        }

        val onClick = confirmation.getOnClick();

        if (onClick != null) {
            onClick.accept((Player) event.getWhoClicked(), event);
        }

        val view = event.getView();
        val inventory = Confirmation.getInventory(view, event.getRawSlot());
        if (inventory == null) {
            return;
        }

        val onTopClick = confirmation.getOnTopClick();
        if (inventory.equals(view.getTopInventory())) {
            onTopClick.accept((Player) event.getWhoClicked(), event);
        }

        val onBottomClick = confirmation.getOnBottomClick();
        if (inventory.equals(view.getBottomInventory())) {
            onBottomClick.accept((Player) event.getWhoClicked(), event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof Confirmation)) {
            return;
        }
        val confirmation = (Confirmation) event.getInventory().getHolder();
        val onOpen = confirmation.getOnOpen();
        if(onOpen != null) {
            onOpen.accept((Player) event.getPlayer(), event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof Confirmation)) {
            return;
        }
        val confirmation = (Confirmation) event.getInventory().getHolder();
        val onClose = confirmation.getOnClose();
        if (onClose != null) {
            onClose.accept((Player) event.getPlayer(), event);
        }

    }
}
