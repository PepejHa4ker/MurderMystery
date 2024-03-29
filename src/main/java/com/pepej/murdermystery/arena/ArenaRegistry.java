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

package com.pepej.murdermystery.arena;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.special.SpecialBlock;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import com.pepej.murdermystery.utils.serialization.LocationSerializer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom on 27/07/2014.
 */
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)

public class ArenaRegistry {

  static MurderMystery plugin = JavaPlugin.getPlugin(MurderMystery.class);
  static List<Arena> arenas = new ArrayList<>();

  /**
   * Checks if player is in any arena
   *
   * @param player player to check
   * @return [b]true[/b] when player is in arena, [b]false[/b] if otherwise
   */
  public static boolean isInArena(Player player) {
    for (Arena arena : arenas) {
      if (arena.getPlayers().contains(player)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns arena where the player is
   *
   * @param p target player
   * @return Arena or null if not playing
   * @see #isInArena(Player) to check if player is playing
   */
  public static Arena getArena(Player p) {
    if (p == null || !p.isOnline()) {
      return null;
    }
    for (Arena arena : arenas) {
      for (Player player : arena.getPlayers()) {
        if (player.getUniqueId().equals(p.getUniqueId())) {
          return arena;
        }
      }
    }
    return null;
  }

  /**
   * Returns arena based by ID
   *
   * @param id name of arena
   * @return Arena or null if not found
   */
  public static Arena getArena(String id) {
    Arena arena = null;
    for (Arena loopArena : arenas) {
      if (loopArena.getId().equalsIgnoreCase(id)) {
        arena = loopArena;
        break;
      }
    }
    return arena;
  }

  public static void registerArena(Arena arena) {
    arenas.add(arena);
  }

  public static void unregisterArena(Arena arena) {
    arenas.remove(arena);
  }

  public static void registerArenas() {
    if (ArenaRegistry.getArenas().size() > 0) {
      for (Arena arena : ArenaRegistry.getArenas()) {
        arena.cleanUpArena();
      }
      for (Arena arena : new ArrayList<>(ArenaRegistry.getArenas())) {
        unregisterArena(arena);
      }
    }
    FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");

    if (!config.contains("instances")) {
      Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.No-Instances-Created"));
      return;
    }

    ConfigurationSection section = config.getConfigurationSection("instances");
    if (section == null) {
      Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.No-Instances-Created"));
      return;
    }
    for (String id : section.getKeys(false)) {
      Arena arena;
      String s = "instances." + id + ".";
      if (s.contains("default")) {
        continue;
      }
      arena = new Arena(id);
      arena.setMinimumPlayers(config.getInt(s + "minimumplayers", 2));
      arena.setMaximumPlayers(config.getInt(s + "maximumplayers", 4));
      arena.setMapName(config.getString(s + "mapname", "none"));
      arena.setSpawnGoldTime(config.getInt(s + "spawngoldtime", 5));
      arena.setHideChances(config.getBoolean(s + "hidechances", false));
      arena.setMurderers(config.getInt(s + "playerpermurderer", 5));
      arena.setDetectives(config.getInt(s + "playerperdetective", 7));
      List<Location> playerSpawnPoints = new ArrayList<>();
      for (String loc : config.getStringList(s + "playerspawnpoints")) {
        playerSpawnPoints.add(LocationSerializer.getLocation(loc));
      }
      arena.setPlayerSpawnPoints(playerSpawnPoints);
      List<Location> goldSpawnPoints = new ArrayList<>();
      for (String loc : config.getStringList(s + "goldspawnpoints")) {
        goldSpawnPoints.add(LocationSerializer.getLocation(loc));
      }
      arena.setGoldSpawnPoints(goldSpawnPoints);

      List<SpecialBlock> specialBlocks = new ArrayList<>();
      if (config.isSet(s + ".mystery-cauldrons")) {
        for (String loc : config.getStringList(s + ".mystery-cauldrons")) {
          specialBlocks.add(new SpecialBlock(LocationSerializer.getLocation(loc), SpecialBlock.SpecialBlockType.MYSTERY_CAULDRON));
        }
      }
      if (config.isSet(s + ".confessionals")) {
        for (String loc : config.getStringList(s + ".confessionals")) {
          specialBlocks.add(new SpecialBlock(LocationSerializer.getLocation(loc), SpecialBlock.SpecialBlockType.PRAISE_DEVELOPER));
        }
      }
      if (config.isSet(s + ".magicanvils")) {
        for (String loc : config.getStringList(s + ".magicanvils")) {
          specialBlocks.add(new SpecialBlock(LocationSerializer.getLocation(loc), SpecialBlock.SpecialBlockType.MAGIC_ANVIL));
        }
      }
      for (SpecialBlock block : specialBlocks) {
        arena.loadSpecialBlock(block);
      }
      arena.setLobbyLocation(LocationSerializer.getLocation(config.getString(s + "lobbylocation", "world,364.0,63.0,-72.0,0.0,0.0")));
      arena.setEndLocation(LocationSerializer.getLocation(config.getString(s + "Endlocation", "world,364.0,63.0,-72.0,0.0,0.0")));

      if (!config.getBoolean(s + "isdone", false)) {
        Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Invalid-Arena-Configuration").replace("%arena%", id).replace("%error%", "NOT VALIDATED"));
        arena.setReady(false);
        ArenaRegistry.registerArena(arena);
        continue;
      }
      ArenaRegistry.registerArena(arena);
      arena.start();
      Bukkit.getConsoleSender().sendMessage(ChatManager.colorMessage("Validator.Instance-Started").replace("%arena%", id));
    }
  }

  public static List<Arena> getArenas() {
    return arenas;
  }
}
