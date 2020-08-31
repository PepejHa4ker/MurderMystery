

package pl.plajer.murdermystery.commands.arguments.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaManager;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.commands.arguments.ArgumentsRegistry;
import pl.plajer.murdermystery.commands.arguments.data.CommandArgument;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.utils.Utils;

public class LeaveArgument {

  public LeaveArgument(ArgumentsRegistry registry) {
    registry.mapArgument("murdermystery", new CommandArgument("leave", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!registry.getPlugin().getConfig().getBoolean("Disable-Leave-Command", false)) {
          Player player = (Player) sender;
          if (!Utils.checkIsInGameInstance((Player) sender)) {
            return;
          }
          player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Teleported-To-The-Lobby", player));
          if (registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
            registry.getPlugin().getBungeeManager().connectToHub(player);
            return;
          }
          Arena arena = ArenaRegistry.getArena(player);
          ArenaManager.leaveAttempt(player, arena);
        }
      }
    });
  }

}
