
package com.pepej.murdermystery.commands.arguments.admin.arena;

import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.ArenaState;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.commands.arguments.data.LabelData;
import com.pepej.murdermystery.commands.arguments.data.LabeledCommandArgument;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Plajer
 * <p>
 * Created at 18.05.2019
 */
public class ForceStartArgument {

  public ForceStartArgument(ArgumentsRegistry registry) {
    registry.mapArgument("murdermysteryadmin", new LabeledCommandArgument("forcestart", "murdermystery.admin.forcestart", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/mma forcestart", "/mma forcestart", "&7Force starts arena you're in\n&6Permission: &7murdermystery.admin.forcestart")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!Utils.checkIsInGameInstance((Player) sender)) {
          return;
        }
        Arena arena = ArenaRegistry.getArena((Player) sender);
        if (arena.getPlayers().size() < 2) {
          ChatManager.broadcast(arena, ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Waiting-For-Players"), arena.getMinimumPlayers()));
          return;
        }
        if (arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS || arena.getArenaState() == ArenaState.STARTING) {
          arena.setArenaState(ArenaState.STARTING);
          arena.setForceStart(true);
          arena.setTimer(0);
          for (Player p : ArenaRegistry.getArena((Player) sender).getPlayers()) {
            p.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Admin-Messages.Set-Starting-In-To-0"));
          }
        }
      }
    });
  }

}
