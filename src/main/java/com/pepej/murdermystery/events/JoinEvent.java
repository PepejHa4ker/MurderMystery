
package com.pepej.murdermystery.events;


import com.pepej.murdermystery.ConfigPreferences;
import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.handlers.PermissionsManager;
import com.pepej.murdermystery.user.User;
import com.pepej.murdermystery.utils.serialization.InventorySerializer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;


@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class JoinEvent implements Listener {

    MurderMystery plugin;

    public JoinEvent(MurderMystery plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {

    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED) && !plugin.getServer().hasWhitelist()
                || e.getResult() != PlayerLoginEvent.Result.KICK_WHITELIST) {
            return;
        }
        if (e.getPlayer().hasPermission(PermissionsManager.getJoinFullGames())) {
            e.setResult(PlayerLoginEvent.Result.ALLOWED);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        User user = plugin.getUserManager().getUser(event.getPlayer());
        plugin.getUserManager().loadStatistics(user);
        if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
            ArenaRegistry.getArenas().get(0).teleportToLobby(event.getPlayer());
            return;
        }
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (ArenaRegistry.getArena(player) == null) {
                continue;
            }
            player.hidePlayer(event.getPlayer());
            event.getPlayer().hidePlayer(player);
        }

        //load player inventory in case of server crash, file is deleted once loaded so if file was already
        //deleted player won't receive his backup, in case of crash he will get it back
        if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
            InventorySerializer.loadInventory(plugin, event.getPlayer());
        }
    }
}
