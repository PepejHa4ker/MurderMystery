

package com.pepej.murdermystery.commands.arguments.game;

import com.pepej.murdermystery.ConfigPreferences;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaManager;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
