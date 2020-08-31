

package pl.plajer.murdermystery.commands.arguments.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaManager;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.commands.arguments.ArgumentsRegistry;
import pl.plajer.murdermystery.commands.arguments.data.CommandArgument;
import pl.plajer.murdermystery.handlers.ChatManager;


public class JoinArguments {

  public JoinArguments(ArgumentsRegistry registry) {
    //join argument
    registry.mapArgument("murdermystery", new CommandArgument("join", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        for (Arena arena : ArenaRegistry.getArenas()) {
          if (args[1].equalsIgnoreCase(arena.getId())) {
            ArenaManager.joinAttempt((Player) sender, arena);
            return;
          }
        }
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
      }
    });
  }
}
