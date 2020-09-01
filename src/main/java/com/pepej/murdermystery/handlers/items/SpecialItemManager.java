package com.pepej.murdermystery.handlers.items;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;


public class SpecialItemManager {

    private static final HashMap<String, SpecialItem> cachedItems = new HashMap<>();

    public static void addItem(String name, SpecialItem entityItem) {
        cachedItems.put(name, entityItem);
    }

    public static SpecialItem getSpecialItem(String name) {
        return cachedItems.get(name);
    }

    public static String getRelatedSpecialItem(ItemStack itemStack) {
        for (String key : cachedItems.keySet()) {
            SpecialItem entityItem = cachedItems.get(key);
            if (entityItem.getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(itemStack.getItemMeta().getDisplayName())) {
                return key;
            }
        }
        return null;
    }
}
