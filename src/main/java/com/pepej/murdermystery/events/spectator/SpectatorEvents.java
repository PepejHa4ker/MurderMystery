
package com.pepej.murdermystery.events.spectator;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.ArenaState;
import com.pepej.murdermystery.user.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;


@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class SpectatorEvents implements Listener {

  private MurderMystery plugin;

  public SpectatorEvents(MurderMystery plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpectatorTarget(EntityTargetEvent e) {
    if (!(e.getTarget() instanceof Player)) {
      return;
    }
    if (plugin.getUserManager().getUser((Player) e.getTarget()).isSpectator()) {
      e.setCancelled(true);
      e.setTarget(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onSpectatorTarget(EntityTargetLivingEntityEvent e) {
    if (!(e.getTarget() instanceof Player)) {
      return;
    }
    if (plugin.getUserManager().getUser((Player) e.getTarget()).isSpectator()) {
      e.setCancelled(true);
      e.setTarget(null);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockBreak(BlockBreakEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDropItem(PlayerDropItemEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBucketEmpty(PlayerBucketEmptyEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onInteract(PlayerInteractEntityEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onShear(PlayerShearEntityEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onConsume(PlayerItemConsumeEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onFoodLevelChange(FoodLevelChangeEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if (plugin.getUserManager().getUser(player).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamage(EntityDamageEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if (!plugin.getUserManager().getUser(player).isSpectator() || ArenaRegistry.getArena(player) == null) {
      return;
    }
    if (player.getLocation().getY() < 1) {
      player.teleport(ArenaRegistry.getArena(player).getPlayerSpawnPoints().get(0));
    }
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamageByBlock(EntityDamageByBlockEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getEntity();
    if (plugin.getUserManager().getUser(player).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onDamageByEntity(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) {
      return;
    }
    Player player = (Player) event.getDamager();
    if (plugin.getUserManager().getUser(player).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPickup(PlayerPickupItemEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onSpectate(PlayerPickupItemEvent event) {
    if (plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onSpectate(PlayerDropItemEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if (arena == null) {
      return;
    }
    if (arena.getArenaState() != ArenaState.IN_GAME
      || plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onInteractEntityInteract(PlayerInteractEntityEvent event) {
    User user = plugin.getUserManager().getUser(event.getPlayer());
    if (user.isSpectator()) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public void onRightClick(PlayerInteractEvent event) {
    Arena arena = ArenaRegistry.getArena(event.getPlayer());
    if (arena != null && plugin.getUserManager().getUser(event.getPlayer()).isSpectator()) {
      event.setCancelled(true);
    }
  }

}
