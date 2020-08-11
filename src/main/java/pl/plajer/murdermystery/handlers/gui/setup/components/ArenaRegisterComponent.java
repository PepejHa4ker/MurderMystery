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

package pl.plajer.murdermystery.handlers.gui.setup.components;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.arena.special.SpecialBlock;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.gui.setup.SetupInventory;
import pl.plajer.murdermystery.handlers.sign.ArenaSign;
import pl.plajer.murdermystery.utils.compat.XMaterial;
import pl.plajer.murdermystery.utils.config.ConfigUtils;
import pl.plajer.murdermystery.utils.items.ItemBuilder;
import pl.plajer.murdermystery.utils.serialization.LocationSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 25.05.2019
 */
public class ArenaRegisterComponent implements ArenaSetupGuiComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    FileConfiguration config = setupInventory.getConfig();
    Main plugin = setupInventory.getPlugin();
    ItemStack registeredItem;
    if (!setupInventory.getArena().isReady()) {
      registeredItem = new ItemBuilder(XMaterial.FIREWORK_ROCKET.parseItem())
        .name(ChatManager.colorRawMessage("&e&lЗарегистрировать арену - Завершить настройку."))
        .lore(ChatColor.GRAY + "Кликните, когда вы закончите с настройкой..")
        .lore(ChatColor.GRAY + "Это проверит и зарегистрирует арену.")
        .build();
    } else {
      registeredItem = new ItemBuilder(Material.BARRIER)
        .name(ChatManager.colorRawMessage("&a&lАрена зарегистрированна - Поздравляем!"))
        .lore(ChatColor.GRAY + "Эта арена уже зарегистрированна!")
        .lore(ChatColor.GRAY + "Молодец, вы прошли всю настройку!")
        .lore(ChatColor.GRAY + "Теперь Вы можете играть на арене!")
        .build();
    }
    pane.addItem(new GuiItem(registeredItem, e -> {
      Arena arena = setupInventory.getArena();
      if (arena.isReady()) {
        return;
      }
      e.getWhoClicked().closeInventory();
      if (ArenaRegistry.getArena(setupInventory.getArena().getId()).isReady()) {
        e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&a&l✔ &aЭта арена уже проверенна и готова к использованию!"));
        return;
      }
      String[] locations = new String[] {"lobbylocation", "Endlocation"};
      String[] spawns = new String[] {"goldspawnpoints", "playerspawnpoints"};
      FileConfiguration arenasConfig = ConfigUtils.getConfig(plugin, "arenas");
      for (String s : locations) {
        if (!arenasConfig.isSet("instances." + arena.getId() + "." + s) || arenasConfig.getString("instances." + arena.getId() + "." + s).equals(LocationSerializer.locationToString(Bukkit.getWorlds().get(0).getSpawnLocation()))) {
          e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&c&l✘ &cОшибка во время проверки арены! Пожалуйста, настройте следующий спавн правильно: " + s + " (не может быть спавном в мире)"));
          return;
        }
      }
      for (String s : spawns) {
        if (!arenasConfig.isSet("instances." + arena.getId() + "." + s) || arenasConfig.getStringList("instances." + arena.getId() + "." + s).size() < 4) {
          e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&c&l✘ &cОшибка во время проверки арены! настройте следующий спавн правильно: " + s + " (должно быть минимум 4 спавна)"));
          return;
        }
      }
      e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&a&l✔ &aПроверка арены прошла успешно! Регистрация нового экземпляра арены: " + arena.getId()));
      config.set("instances." + arena.getId() + ".isdone", true);
      ConfigUtils.saveConfig(plugin, config, "arenas");
      List<Sign> signsToUpdate = new ArrayList<>();
      ArenaRegistry.unregisterArena(setupInventory.getArena());

      for (ArenaSign arenaSign : plugin.getSignManager().getArenaSigns()) {
        if (arenaSign.getArena().equals(setupInventory.getArena())) {
          signsToUpdate.add(arenaSign.getSign());
        }
      }
      arena = new Arena(setupInventory.getArena().getId());
      arena.setReady(true);
      List<Location> playerSpawnPoints = new ArrayList<>();
      for (String loc : config.getStringList("instances." + arena.getId() + ".playerspawnpoints")) {
        playerSpawnPoints.add(LocationSerializer.getLocation(loc));
      }
      arena.setPlayerSpawnPoints(playerSpawnPoints);
      List<Location> goldSpawnPoints = new ArrayList<>();
      for (String loc : config.getStringList("instances." + arena.getId() + ".goldspawnpoints")) {
        goldSpawnPoints.add(LocationSerializer.getLocation(loc));
      }
      arena.setGoldSpawnPoints(goldSpawnPoints);

      List<SpecialBlock> specialBlocks = new ArrayList<>();
      if (config.isSet("instances." + arena.getId() + ".mystery-cauldrons")) {
        for (String loc : config.getStringList("instances." + arena.getId() + ".mystery-cauldrons")) {
          specialBlocks.add(new SpecialBlock(LocationSerializer.getLocation(loc), SpecialBlock.SpecialBlockType.MYSTERY_CAULDRON));
        }
      }
      if (config.isSet("instances." + arena.getId() + ".confessionals")) {
        for (String loc : config.getStringList("instances." + arena.getId() + ".confessionals")) {
          specialBlocks.add(new SpecialBlock(LocationSerializer.getLocation(loc), SpecialBlock.SpecialBlockType.PRAISE_DEVELOPER));
        }
      }
      for (SpecialBlock specialBlock : specialBlocks) {
        if (arena.getSpecialBlocks().contains(specialBlock)) {
          continue;
        }
        arena.loadSpecialBlock(specialBlock);
      }
      arena.setMinimumPlayers(config.getInt("instances." + arena.getId() + ".minimumplayers"));
      arena.setMaximumPlayers(config.getInt("instances." + arena.getId() + ".maximumplayers"));
      arena.setMapName(config.getString("instances." + arena.getId() + ".mapname"));
      arena.setLobbyLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".lobbylocation")));
      arena.setEndLocation(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".Endlocation")));
      config.set("instances." + arena.getId() + ".hidechances", false);
      ArenaRegistry.registerArena(arena);
      arena.start();
      for (Sign s : signsToUpdate) {
        plugin.getSignManager().getArenaSigns().add(new ArenaSign(s, arena));
      }
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 8, 0);
  }

  @Override
  public void show(Player player) {

  }

}
