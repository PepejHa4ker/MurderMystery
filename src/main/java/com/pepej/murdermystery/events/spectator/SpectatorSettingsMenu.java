package com.pepej.murdermystery.events.spectator;

import com.pepej.murdermystery.utils.compat.XMaterial;
import com.pepej.murdermystery.utils.items.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpectatorSettingsMenu implements Listener {

    private final String inventoryName;
    private final String speedOptionName;
    private Inventory inv;

    public SpectatorSettingsMenu(JavaPlugin plugin, String inventoryName, String speedOptionName) {
        this.inventoryName = inventoryName;
        this.speedOptionName = speedOptionName;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        initInventory();
    }

    public void openSpectatorSettingsMenu(Player player) {
        player.openInventory(this.inv);
    }

    @EventHandler
    public void onSpectatorMenuClick(InventoryClickEvent e) {
        if (e.getInventory() == null || !e.getView().getTitle().equals(color(inventoryName))) {
            return;
        }
        if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        p.closeInventory();
        int amp;
        switch (e.getCurrentItem().getType()) {
            case LEATHER_BOOTS:
                amp = 0;
                break;
            case CHAINMAIL_BOOTS:
                amp = 1;
                break;
            case IRON_BOOTS:
                amp = 2;
                break;
            case GOLD_BOOTS:
                amp = 3;
                break;
            case DIAMOND_BOOTS:
                amp = 4;
                break;
          default:
            throw new IllegalStateException("Unexpected value: " + e.getCurrentItem().getType());
        }
      p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, amp, false, false));
    }

    private void initInventory() {
        Inventory inv = Bukkit.createInventory(null, 9 * 4, inventoryName);
        inv.setItem(11, new ItemBuilder(Material.LEATHER_BOOTS)
                .name(color(speedOptionName + " I")).build());
        inv.setItem(12, new ItemBuilder(Material.CHAINMAIL_BOOTS)
                .name(color(speedOptionName + " II")).build());
        inv.setItem(13, new ItemBuilder(Material.IRON_BOOTS)
                .name(color(speedOptionName + " III")).build());
        inv.setItem(14, new ItemBuilder(XMaterial.GOLDEN_BOOTS.parseItem())
                .name(color(speedOptionName + " IV")).build());
        inv.setItem(15, new ItemBuilder(Material.DIAMOND_BOOTS)
                .name(color(speedOptionName + " V")).build());
        this.inv = inv;
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}
