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

package pl.plajer.murdermystery.handlers.gui.setup;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.val;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.gui.setup.components.*;
import pl.plajer.murdermystery.utils.config.ConfigUtils;

import java.util.Random;

/**
 * @author Plajer
 * <p>
 * Created at 25.05.2019
 */
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class SetupInventory {

  public static final String VIDEO_LINK = "https://tutorial.plajer.xyz";
  static Random random = new Random();
  static MurderMystery plugin = JavaPlugin.getPlugin(MurderMystery.class);
  @Getter
  FileConfiguration config = ConfigUtils.getConfig(plugin, "arenas");
  @Getter
  Arena arena;
  @Getter
  Player player;
  @Getter
  @NonFinal
  Gui gui;
  @Getter
  SetupUtilities setupUtilities;

  public SetupInventory(Arena arena, Player player) {
    this.arena = arena;
    this.player = player;
    this.setupUtilities = new SetupUtilities(config, arena);
    prepareGui();
  }

  private void prepareGui() {
    this.gui = new Gui(plugin, 4, "MurderMystery настройка арены");
    this.gui.setOnGlobalClick(e -> e.setCancelled(true));
    StaticPane pane = new StaticPane(9, 4);
    this.gui.addPane(pane);
    prepareComponents(pane);
  }

  private void prepareComponents(StaticPane pane) {
    val spawnComponents = new SpawnComponents();
    spawnComponents.prepare(this);
    spawnComponents.injectComponents(pane);

    val playerAmountComponents = new PlayerAmountComponents();
    playerAmountComponents.prepare(this);
    playerAmountComponents.injectComponents(pane);

    val miscComponents = new MiscComponents();
    miscComponents.prepare(this);
    miscComponents.injectComponents(pane);

    val arenaRegisterComponent = new ArenaRegisterComponent();
    arenaRegisterComponent.prepare(this);
    arenaRegisterComponent.injectComponents(pane);

    val specialBlocksComponents = new SpecialBlocksComponents();
    specialBlocksComponents.prepare(this);
    specialBlocksComponents.injectComponents(pane);
  }

  private void sendProTip(Player p) {
    val rand = random.nextInt(16 + 1);
    switch (rand) {
      case 0:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Help us translating plugin to your language here: https://translate.plajer.xyz"));
        break;
      case 1:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7LeaderHeads leaderboard plugin is supported with our plugin! Check here: https://bit.ly/2IH5zkR"));
        break;
      case 2:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7We are open source! You can always help us by contributing! Check https://github.com/Plajer-Lair/MurderMystery"));
        break;
      case 3:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Need help? Check wiki &8https://wiki.plajer.xyz/minecraft/murdermystery &7or discord https://discord.gg/UXzUdTP"));
        break;
      case 4:
        p.sendMessage(ChatManager.colorRawMessage("&e&lTIP: &7Suggest new ideas for the plugin or vote on current ones! https://uservoice.plajer.xyz/index.php?id=MurderMystery"));
        break;
      default:
        break;
    }
  }

  public void openInventory() {
    sendProTip(player);
    gui.show(player);
  }
}
