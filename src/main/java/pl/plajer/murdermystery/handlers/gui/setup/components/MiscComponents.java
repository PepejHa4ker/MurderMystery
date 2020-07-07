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

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.gui.setup.SetupInventory;
import pl.plajer.murdermystery.handlers.sign.ArenaSign;
import pl.plajer.murdermystery.utils.conversation.SimpleConversationBuilder;
import pl.plajerlair.commonsbox.minecraft.compat.XMaterial;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;
import pl.plajerlair.commonsbox.minecraft.serialization.LocationSerializer;

/**
 * @author Plajer
 * <p>
 * Created at 25.05.2019
 *
 */
public class MiscComponents implements ArenaSetupGuiComponent {

  private SetupInventory setupInventory;

  @Override
  public void prepare(SetupInventory setupInventory) {
    this.setupInventory = setupInventory;
  }

  @Override
  public void injectComponents(StaticPane pane) {
    Player player = setupInventory.getPlayer();
    FileConfiguration config = setupInventory.getConfig();
    Arena arena = setupInventory.getArena();
    Main plugin = SetupInventory.getPlugin();
    ItemStack bungeeItem;
    if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
      bungeeItem = new ItemBuilder(Material.SIGN)
        .name(ChatManager.colorRawMessage("&e&lДобавить табличку"))
        .lore(ChatColor.GRAY + "Наведитесь на табличку и кликните сюда.")
        .lore(ChatColor.DARK_GRAY + "(это установит целевую табличку как игровую табличку)")
        .build();
    } else {
      bungeeItem = new ItemBuilder(Material.BARRIER)
        .name(ChatManager.colorRawMessage("&c&lДобавить табличку"))
        .lore(ChatColor.GRAY + "Опция отключена в банджи режиме.")
        .lore(ChatColor.DARK_GRAY + "Режим банджи предназначен для одной арены на сервер")
        .lore(ChatColor.DARK_GRAY + "Если вы хотите сделать мультиарену, отключите банджи в конфиге!")
        .build();
    }
    pane.addItem(new GuiItem(bungeeItem, e -> {
      if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
        return;
      }
      e.getWhoClicked().closeInventory();
      Location location = player.getTargetBlock(null, 10).getLocation();
      if (!(location.getBlock().getState() instanceof Sign)) {
        player.sendMessage(ChatManager.colorMessage("Commands.Look-Sign"));
        return;
      }
      if (location.distance(e.getWhoClicked().getWorld().getSpawnLocation()) <= Bukkit.getServer().getSpawnRadius()
        && e.getClick() != ClickType.SHIFT_LEFT) {
        e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&c&l✖ &cПредупреждение | Серверная защита спавна установлена на &6" + Bukkit.getServer().getSpawnRadius()
          + " &cи табличка которую вы хотите поставить в этом месте защищена! &c&lНе операторы не смогут взаимодействовать с этой табличкой и не смогут присоединиться к игре, поэтому."));
        e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&cВы можете проигнорировать это предупреждение и добавить табличку с помощью Shift + левый щелчок, но на данный момент операция отменена"));
        return;
      }
      plugin.getSignManager().getArenaSigns().add(new ArenaSign((Sign) location.getBlock().getState(), arena));
      player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Signs.Sign-Created"));
      String signLoc = location.getBlock().getWorld().getName() + "," + location.getBlock().getX() + "," + location.getBlock().getY() + "," + location.getBlock().getZ() + ",0.0,0.0";
      List<String> locs = config.getStringList("instances." + arena.getId() + ".signs");
      locs.add(signLoc);
      config.set("instances." + arena.getId() + ".signs", locs);
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 5, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.NAME_TAG)
      .name(ChatManager.colorRawMessage("&e&lУстановить название карты"))
      .lore(ChatColor.GRAY + "Кликните для установки названия карты")
      .lore("", ChatManager.colorRawMessage("&a&lТекущий: &e" + config.getString("instances." + arena.getId() + ".mapname")))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      new SimpleConversationBuilder().withPrompt(new StringPrompt() {
        @Override
        public String getPromptText(ConversationContext context) {
          return ChatManager.colorRawMessage(ChatManager.PLUGIN_PREFIX + "&eПожалуйста, введине в чат название арены! Вы можете использовать цветовые кода.");
        }

        @Override
        public Prompt acceptInput(ConversationContext context, String input) {
          String name = ChatManager.colorRawMessage(input);
          player.sendRawMessage(ChatManager.colorRawMessage("&e✔ Завершено | &aИмя арены " + arena.getId() + " установленно на " + name));
          arena.setMapName(name);
          config.set("instances." + arena.getId() + ".mapname", arena.getMapName());
          ConfigUtils.saveConfig(plugin, config, "arenas");

          new SetupInventory(arena, player).openInventory();
          return Prompt.END_OF_CONVERSATION;
        }
      }).buildFor(player);
    }), 6, 0);

    pane.addItem(new GuiItem(new ItemBuilder(Material.GOLD_INGOT)
      .name(ChatManager.colorRawMessage("&e&lДобавить спавн золота"))
      .lore(ChatColor.GRAY + "Добавить новый спавн золота")
      .lore(ChatColor.GRAY + "на месте, где вы стоите.")
      .lore("", setupInventory.getSetupUtilities().isOptionDoneList("instances." + arena.getId() + ".goldspawnpoints", 4))
      .lore("", ChatManager.colorRawMessage("&8Shift + Правый клик, чтобы удалить все спавны."))
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      if (e.getClick() == ClickType.SHIFT_RIGHT) {
        config.set("instances." + arena.getId() + ".goldspawnpoints", new ArrayList<>());
        arena.setGoldSpawnPoints(new ArrayList<>());
        player.sendMessage(ChatManager.colorRawMessage("&eГотово | &aТочки спавна золота удаленны, Вы можете добавить их снова!"));
        arena.setReady(false);
        ConfigUtils.saveConfig(plugin, config, "arenas");
        return;
      }
      List<String> goldSpawns = config.getStringList("instances." + arena.getId() + ".goldspawnpoints");
      goldSpawns.add(LocationSerializer.locationToString(player.getLocation()));
      config.set("instances." + arena.getId() + ".goldspawnpoints", goldSpawns);
      String goldProgress = goldSpawns.size() >= 4 ? "&e✔ Завершено | " : "&c✘ Не завершено | ";
      player.sendMessage(ChatManager.colorRawMessage(goldProgress + "&aСпавн золота добавлен! &8(&7" + goldSpawns.size() + "/4&8)"));
      if (goldSpawns.size() == 4) {
        player.sendMessage(ChatManager.colorRawMessage("&eInfo | &aВы можете добавить больше чем 4 спавна золота! 4 это просто минимум!"));
      }
      List<Location> spawns = new ArrayList<>(arena.getGoldSpawnPoints());
      spawns.add(player.getLocation());
      arena.setGoldSpawnPoints(spawns);
      ConfigUtils.saveConfig(plugin, config, "arenas");
    }), 7, 0);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.GOLD_NUGGET.parseItem())
      .amount(config.getInt("instances." + arena.getId() + "." + "spawngoldtime", 3))
      .name(ChatManager.colorRawMessage("&e&lУстановить время спавна золота в секундах"))
      .lore(ChatColor.GRAY + "ЛЕВЫЙ клик чтобы уменьшить")
      .lore(ChatColor.GRAY + "ПРАВЫЙ клик чтобы увеличить")
      .lore(ChatColor.DARK_GRAY + "Как много золота должно спавниться? ")
      .lore(ChatColor.DARK_GRAY + "Это значит 1 золото спавнится каждые ... секунд")
      .lore(ChatColor.DARK_GRAY + "По умолчанию: 5")
      .lore(ChatColor.DARK_GRAY + "Каждые 5 секунд будет спавниться 1 золото")
      .lore("", setupInventory.getSetupUtilities().isOptionDone("instances." + arena.getId() + ".spawngoldtime"))
      .build(), e -> {
      if (e.getClick().isRightClick()) {
        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() + 1);
      }
      if (e.getClick().isLeftClick()) {
        if (e.getCurrentItem().getAmount() > 1) {
          e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
        }
      }
      if (e.getInventory().getItem(e.getSlot()).getAmount() < 1) {
        e.getWhoClicked().sendMessage(ChatManager.colorRawMessage("&c&l✖ &cПредупреждение | Пожалуйста, не ставьте количество меньше чем 1! Игра не работает без золота"));
        e.getInventory().getItem(e.getSlot()).setAmount(1);
      }
      config.set("instances." + arena.getId() + ".spawngoldtime", e.getCurrentItem().getAmount());
      arena.setDetectives(e.getCurrentItem().getAmount());
      ConfigUtils.saveConfig(plugin, config, "arenas");
      new SetupInventory(arena, setupInventory.getPlayer()).openInventory();
    }), 7, 1);

    pane.addItem(new GuiItem(new ItemBuilder(XMaterial.FILLED_MAP.parseItem())
      .name(ChatManager.colorRawMessage("&e&lПосмотреть видео-гайд по настройке"))
      .lore(ChatColor.GRAY + "Возникли проблемы с настройкой или хотите")
      .lore(ChatColor.GRAY + "узнать несколько полезных советов? Нажмите, чтобы получить ссылку на видео!")
      .build(), e -> {
      e.getWhoClicked().closeInventory();
      player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorRawMessage("&6Проверьте это видео: " + SetupInventory.VIDEO_LINK));
    }), 8, 1);
  }

}
