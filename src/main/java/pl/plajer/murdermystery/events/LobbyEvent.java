
package pl.plajer.murdermystery.events;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.arena.ArenaState;

public class LobbyEvent implements Listener {

  public LobbyEvent(MurderMystery plugin) {
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onLobbyDamage(EntityDamageEvent event) {
    if (event.getEntity().getType() != EntityType.PLAYER) {
      return;
    }
    Player player = (Player) event.getEntity();
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null || arena.getArenaState() == ArenaState.IN_GAME) {
      return;
    }
    event.setCancelled(true);
    player.setFireTicks(0);
    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
  }

}
