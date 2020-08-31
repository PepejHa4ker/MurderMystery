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
import pl.plajer.murdermystery.handlers.gui.setup.components.*;
import pl.plajer.murdermystery.utils.config.ConfigUtils;

@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class SetupInventory {

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

  public void openInventory() {
    gui.show(player);
  }
}
