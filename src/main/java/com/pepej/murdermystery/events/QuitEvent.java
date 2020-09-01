
package com.pepej.murdermystery.events;


import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.arena.ArenaManager;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.user.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class QuitEvent implements Listener {

  private MurderMystery plugin;

  public QuitEvent(MurderMystery plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    if (ArenaRegistry.getArena(event.getPlayer()) != null) {
      ArenaManager.leaveAttempt(event.getPlayer(), ArenaRegistry.getArena(event.getPlayer()));
    }
    final User user = plugin.getUserManager().getUser(event.getPlayer());
    //May stats update on quit will be removed in further release as we save it on ending stage now
    for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
      plugin.getUserManager().saveStatistic(user, stat);
    }
    plugin.getUserManager().removeUser(user);
  }

}
