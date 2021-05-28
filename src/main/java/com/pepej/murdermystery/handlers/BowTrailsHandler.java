package com.pepej.murdermystery.handlers;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.ArenaRegistry;
import org.bukkit.Particle;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;


public class BowTrailsHandler implements Listener {

  private final MurderMystery plugin;
  private final Map<String, Particle> registeredTrails = new HashMap<>();

  public BowTrailsHandler(MurderMystery plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    registerBowTrail("murdermystery.trails.heart", Particle.HEART);
    registerBowTrail("murdermystery.trails.flame", Particle.FLAME);
    registerBowTrail("murdermystery.trails.critical", Particle.CRIT);
    registerBowTrail("murdermystery.trails.cloud", Particle.CLOUD);
  }

  public void registerBowTrail(String permission, Particle particle) {
    registeredTrails.put(permission, particle);
  }

  @EventHandler
  public void onArrowShoot(EntityShootBowEvent e) {
    if (!(e.getEntity() instanceof Player && e.getProjectile() instanceof Arrow)) {
      return;
    }
    if (!ArenaRegistry.isInArena((Player) e.getEntity()) || e.getProjectile() == null || e.getProjectile().isDead() || e.getProjectile().isOnGround()) {
      return;
    }
    for (String perm : registeredTrails.keySet()) {
      if (e.getEntity().hasPermission(perm)) {
        new BukkitRunnable() {
          @Override
          public void run() {
            if (e.getProjectile() == null || e.getProjectile().isDead() || e.getProjectile().isOnGround()) {
              this.cancel();
            }
            e.getProjectile().getWorld().spawnParticle(registeredTrails.get(perm), e.getProjectile().getLocation(), 3, 0, 0, 0, 0);
          }
        }.runTaskTimer(plugin, 0, 0);
        break;
      }
    }
  }

}
