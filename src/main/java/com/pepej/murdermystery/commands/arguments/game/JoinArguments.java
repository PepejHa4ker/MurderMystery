

package com.pepej.murdermystery.commands.arguments.game;

import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaManager;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.handlers.ChatManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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
