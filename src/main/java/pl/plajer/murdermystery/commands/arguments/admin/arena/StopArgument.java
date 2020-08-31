

package pl.plajer.murdermystery.commands.arguments.admin.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.arena.ArenaManager;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.arena.ArenaState;
import pl.plajer.murdermystery.commands.arguments.ArgumentsRegistry;
import pl.plajer.murdermystery.commands.arguments.data.CommandArgument;
import pl.plajer.murdermystery.commands.arguments.data.LabelData;
import pl.plajer.murdermystery.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.murdermystery.utils.Utils;

/**
 * @author Plajer
 * <p>
 * Created at 18.05.2019
 */
public class StopArgument {

  public StopArgument(ArgumentsRegistry registry) {
    registry.mapArgument("murdermysteryadmin", new LabeledCommandArgument("stop", "murdermystery.admin.stop", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/mma stop", "/mma stop", "&7Stops the arena you're in\n&7&lYou must be in target arena!\n&6Permission: &7murdermystery.admin.stop")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkIsInGameInstance((Player) sender)) {
          return;
        }
        if (ArenaRegistry.getArena((Player) sender).getArenaState() != ArenaState.ENDING) {
          ArenaManager.stopGame(true, ArenaRegistry.getArena((Player) sender));
        }
      }
    });
  }

}
