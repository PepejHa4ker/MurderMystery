package com.pepej.murdermystery.commands.arguments.game;

import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.special.SpecialBlock;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.commands.arguments.data.LabelData;
import com.pepej.murdermystery.commands.arguments.data.LabeledCommandArgument;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import com.pepej.murdermystery.utils.serialization.LocationSerializer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class CreateArgument {

  ArgumentsRegistry registry;

  public CreateArgument(ArgumentsRegistry registry) {
    this.registry = registry;
    registry.mapArgument("murdermystery", new LabeledCommandArgument("create", "murdermystery.admin.create", CommandArgument.ExecutorType.PLAYER,
            new LabelData("/mm create &6<arena>", "/mm create <arena>", "&7Create new arena\n&6Permission: &7murdermystery.admin.create")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        Player player = (Player) sender;
        for (Arena arena : ArenaRegistry.getArenas()) {
          if (arena.getId().equalsIgnoreCase(args[1])) {
            player.sendMessage(ChatColor.DARK_RED + "Arena with that ID already exists!");
            player.sendMessage(ChatColor.DARK_RED + "Usage: /mm create <ID>");
            return;
          }
        }
        if (ConfigUtils.getConfig(registry.getPlugin(), "arenas").contains("instances." + args[1])) {
          player.sendMessage(ChatColor.DARK_RED + "Instance/Arena already exists! Use another ID or delete it first!");
        } else {
          createInstanceInConfig(args[1], player.getWorld().getName());
          player.sendMessage(ChatColor.BOLD + "------------------------------------------");
          player.sendMessage(ChatColor.YELLOW + "      Instance " + args[1] + " created!");
          player.sendMessage(ChatColor.BOLD + "------------------------------------------- ");
        }
      }
    });
  }

  private void createInstanceInConfig(String id, String worldName) {
    String path = "instances." + id + ".";
    FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");
    LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", path + "lobbylocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", path + "Startlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    LocationSerializer.saveLoc(registry.getPlugin(), config, "arenas", path + "Endlocation", Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
    config.set(path + "playerspawnpoints", new ArrayList<>());
    config.set(path + "goldspawnpoints", new ArrayList<>());
    config.set(path + "minimumplayers", 2);
    config.set(path + "maximumplayers", 10);
    config.set(path + "playerpermurderer", 5);
    config.set(path + "playerperdetective", 7);
    config.set(path + "mapname", id);
    config.set(path + "signs", new ArrayList<>());
    config.set(path + "isdone", false);
    config.set(path + "world", worldName);
    config.set(path + "mystery-cauldrons", new ArrayList<>());
    config.set(path + "confessionals", new ArrayList<>());
    ConfigUtils.saveConfig(registry.getPlugin(), config, "arenas");

    Arena arena = new Arena(id);

    List<Location> playerSpawnPoints = new ArrayList<>();
    for (String loc : config.getStringList(path + "playerspawnpoints")) {
      playerSpawnPoints.add(LocationSerializer.getLocation(loc));
    }
    arena.setPlayerSpawnPoints(playerSpawnPoints);
    List<Location> goldSpawnPoints = new ArrayList<>();
    for (String loc : config.getStringList(path + "goldspawnpoints")) {
      goldSpawnPoints.add(LocationSerializer.getLocation(loc));
    }
    arena.setGoldSpawnPoints(goldSpawnPoints);

    List<SpecialBlock> specialBlocks = new ArrayList<>();
    if (config.isSet("instances." + arena.getId() + ".mystery-cauldrons")) {
      for (String loc : config.getStringList("instances." + arena.getId() + ".mystery-cauldrons")) {
        specialBlocks.add(new SpecialBlock(LocationSerializer.getLocation(loc), SpecialBlock.SpecialBlockType.MYSTERY_CAULDRON));
      }
    }
    for (SpecialBlock block : specialBlocks) {
      arena.loadSpecialBlock(block);
    }
    arena.setMinimumPlayers(config.getInt(path + "minimumplayers"));
    arena.setMaximumPlayers(config.getInt(path + "maximumplayers"));
    arena.setDetectives(config.getInt(path + "playerperdetective"));
    arena.setMurderers(config.getInt(path + "playerpermurderer"));
    arena.setMapName(config.getString(path + "mapname"));
    arena.setLobbyLocation(LocationSerializer.getLocation(config.getString(path + "lobbylocation")));
    arena.setEndLocation(LocationSerializer.getLocation(config.getString(path + "Endlocation")));
    arena.setReady(false);

    ArenaRegistry.registerArena(arena);
  }

}