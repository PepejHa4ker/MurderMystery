package com.pepej.murdermystery.handlers.items;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.utils.compat.XMaterial;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SpecialItem {

  private ItemStack itemStack;
  private int slot;
  private final String name;

  public SpecialItem(String name) {
    this.name = name;
  }

  public static void loadAll() {
    new SpecialItem("Leave").load(ChatColor.RED + "Leave", new String[] {ChatColor.GRAY + "Нажмите для телепортации в лобби"}, XMaterial.WHITE_BED.parseMaterial(), 8);
    new SpecialItem("Start").load(ChatColor.GREEN + "Начать игру", new String[] {ChatColor.GRAY + "Нажмите для начала игры\n(требуется {player} для начала игры)"}, XMaterial.DIAMOND.parseMaterial(), 4);
    new SpecialItem("Menu").load(ChatColor.GOLD + "Меню", new String[]{ChatColor.GRAY + "Нажмите для открытия!"}, XMaterial.CHEST.parseMaterial(), 0);
    new SpecialItem("Perks").load(ChatColor.DARK_PURPLE + "Способности", new String[]{ChatColor.GRAY + "Нажмите для открытия!"}, XMaterial.ENDER_EYE.parseMaterial(), 1);
  }

  public void load(String displayName, String[] lore, Material material, int slot) {
    FileConfiguration config = ConfigUtils.getConfig(JavaPlugin.getPlugin(MurderMystery.class), "lobbyitems");

    if (!config.contains(name)) {
      config.set(name + ".data", 0);
      config.set(name + ".displayname", displayName);
      config.set(name + ".lore", Arrays.asList(lore));
      config.set(name + ".material-name", material.toString());
      config.set(name + ".slot", slot);
    }
    ConfigUtils.saveConfig(JavaPlugin.getPlugin(MurderMystery.class), config, "lobbyitems");
    ItemStack stack = XMaterial.fromString(config.getString(name + ".material-name").toUpperCase()).parseItem();
    ItemMeta meta = stack.getItemMeta();
    meta.setDisplayName(ChatManager.colorRawMessage(config.getString(name + ".displayname")));

    List<String> colorizedLore = new ArrayList<>();
    for (String str : config.getStringList(name + ".lore")) {
      colorizedLore.add(ChatManager.colorRawMessage(str));
    }
    meta.setLore(colorizedLore);
    stack.setItemMeta(meta);

    SpecialItem item = new SpecialItem(name);
    item.itemStack = stack;
    item.slot = config.getInt(name + ".slot");
    SpecialItemManager.addItem(name, item);
  }

  public int getSlot() {
    return slot;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }
}
