/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.murdermystery.events;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.handlers.PermissionsManager;
import pl.plajer.murdermystery.user.User;
import pl.plajer.murdermystery.utils.serialization.InventorySerializer;


/**
 * @author Plajer
 * <p>
 * Created at 03.08.2018
 */
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

        plugin.getUserManager().loadStatistics(user);
        //load player inventory in case of server crash, file is deleted once loaded so if file was already
        //deleted player won't receive his backup, in case of crash he will get it back
        if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
            InventorySerializer.loadInventory(plugin, event.getPlayer());
        }
    }
}
