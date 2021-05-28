

package com.pepej.murdermystery.events.spectator;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.role.Role;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.utils.Utils;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.Set;

@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)

public class SpectatorItemEvents implements Listener {

  private MurderMystery plugin;
  private SpectatorSettingsMenu spectatorSettingsMenu;
  private boolean usesPaperSpigot = Bukkit.getServer().getVersion().contains("Paper");

  public SpectatorItemEvents(MurderMystery plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    spectatorSettingsMenu = new SpectatorSettingsMenu(plugin, ChatManager.colorMessage("In-Game.Spectator.Settings-Menu.Inventory-Name"),
      ChatManager.colorMessage("In-Game.Spectator.Settings-Menu.Speed-Name"));
  }

  @EventHandler
  public void onSpectatorItemClick(PlayerInteractEvent e) {
    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() != Action.PHYSICAL) {
      if (ArenaRegistry.getArena(e.getPlayer()) == null) {
        return;
      }
      ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
      if (stack == null || !stack.hasItemMeta() || !stack.getItemMeta().hasDisplayName()) {
        return;
      }
      if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name"))) {
        e.setCancelled(true);
        openSpectatorMenu(e.getPlayer().getWorld(), e.getPlayer());
      } else if (stack.getItemMeta().getDisplayName().equalsIgnoreCase(ChatManager.colorMessage("In-Game.Spectator.Settings-Menu.Item-Name"))) {
        e.setCancelled(true);
        spectatorSettingsMenu.openSpectatorSettingsMenu(e.getPlayer());
      }
    }
  }

  private void openSpectatorMenu(World world, Player p) {
    Inventory inventory = plugin.getServer().createInventory(null, Utils.serializeInt(ArenaRegistry.getArena(p).getPlayers().size()),
      ChatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name"));
    FileConfiguration config = ConfigUtils.getConfig(plugin, "config");
    Set<Player> players = ArenaRegistry.getArena(p).getPlayers();
    for (Player player : world.getPlayers()) {
      if (players.contains(player) && !plugin.getUserManager().getUser(player).isSpectator()) {
        ItemStack skull;
        skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getName());
        String role = ChatManager.colorMessage("In-Game.Spectator.Target-Player-Role");
        if (Role.isRole(Role.MURDERER, player)) {
          role = StringUtils.replace(role, "%ROLE%", ChatManager.colorMessage("Scoreboard.Roles.Murderer"));
        } else if (Role.isRole(Role.ANY_DETECTIVE, player)) {
          role = StringUtils.replace(role, "%ROLE%", ChatManager.colorMessage("Scoreboard.Roles.Detective"));
        } else {
          role = StringUtils.replace(role, "%ROLE%", ChatManager.colorMessage("Scoreboard.Roles.Innocent"));
        }
        if(config.getBoolean("Show-Players-Role")) {
          meta.setLore(Collections.singletonList(role));
        } else {
          String hide = "§c§lСкрыто";
          meta.setLore(Collections.singletonList(hide));
        }
        skull.setDurability((short) SkullType.PLAYER.ordinal());
        skull.setItemMeta(meta);
        inventory.addItem(skull);
      }
    }
    p.openInventory(inventory);
  }

  @EventHandler
  public void onSpectatorInventoryClick(InventoryClickEvent e) {
    Player p = (Player) e.getWhoClicked();
    if (ArenaRegistry.getArena(p) == null) {
      return;
    }
    Arena arena = ArenaRegistry.getArena(p);
    if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta()
      || !e.getCurrentItem().getItemMeta().hasDisplayName() || !e.getCurrentItem().getItemMeta().hasLore()) {
      return;
    }
    if (!e.getView().getTitle().equalsIgnoreCase(ChatManager.colorMessage("In-Game.Spectator.Spectator-Menu-Name", p))) {
      return;
    }
    e.setCancelled(true);
    ItemMeta meta = e.getCurrentItem().getItemMeta();
    for (Player player : arena.getPlayers()) {
      if (player.getName().equalsIgnoreCase(meta.getDisplayName()) || ChatColor.stripColor(meta.getDisplayName()).contains(player.getName())) {
        p.sendMessage(ChatManager.formatMessage(arena, ChatManager.colorMessage("Commands.Admin-Commands.Teleported-To-Player"), player));
        p.teleport(player);
        p.closeInventory();
        return;
      }
    }
  }

}
