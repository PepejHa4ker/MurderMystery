

package com.pepej.murdermystery.commands.arguments.admin;

import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.commands.arguments.data.LabelData;
import com.pepej.murdermystery.commands.arguments.data.LabeledCommandArgument;
import com.pepej.murdermystery.handlers.ChatManager;
import org.bukkit.command.CommandSender;

/**
 * @author Plajer
 * <p>
 * Created at 18.05.2019
 */
public class ListArenasArgument {

  public ListArenasArgument(ArgumentsRegistry registry) {
    registry.mapArgument("murdermysteryadmin", new LabeledCommandArgument("list", "murdermystery.admin.list", CommandArgument.ExecutorType.BOTH,
      new LabelData("/mma list", "/mma list", "&7Shows list with all loaded arenas\n&6Permission: &7murdermystery.admin.list")) {
      @Override
      public void execute(CommandSender sender, String[] args) {

        sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Header"));
        int i = 0;
        for (Arena arena : ArenaRegistry.getArenas()) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.Format").replace("%arena%", arena.getId())
                                        .replace("%status%", arena.getArenaState().getFormattedName()).replace("%players%", String.valueOf(arena.getPlayers().size()))
                                        .replace("%maxplayers%", String.valueOf(arena.getMaximumPlayers())));
          i++;
        }
        if (i == 0) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Admin-Commands.List-Command.No-Arenas"));
        }
      }
    });
  }

}
