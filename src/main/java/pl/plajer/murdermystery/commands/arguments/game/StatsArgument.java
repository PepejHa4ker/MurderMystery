package pl.plajer.murdermystery.commands.arguments.game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.commands.arguments.ArgumentsRegistry;
import pl.plajer.murdermystery.commands.arguments.data.CommandArgument;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.user.User;


public class StatsArgument {

  public StatsArgument(ArgumentsRegistry registry) {
    registry.mapArgument("murdermystery", new CommandArgument("stats", "", CommandArgument.ExecutorType.PLAYER) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        Player player = args.length == 2 ? Bukkit.getPlayerExact(args[1]) : (Player) sender;
        if (player == null) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Player-Not-Found"));
          return;
        }
        User user = registry.getPlugin().getUserManager().getUser(player);
        if (player.equals(sender)) {
          sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header", player));
        } else {
          sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Header-Other", player).replace("%player%", player.getName()));
        }
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Kills", player) + user.getStat(StatsStorage.StatisticType.KILLS));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Deaths", player) + user.getStat(StatsStorage.StatisticType.DEATHS));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Wins", player) + user.getStat(StatsStorage.StatisticType.WINS));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Loses", player) + user.getStat(StatsStorage.StatisticType.LOSES));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Games-Played", player) + user.getStat(StatsStorage.StatisticType.GAMES_PLAYED));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Highest-Score", player) + user.getStat(StatsStorage.StatisticType.HIGHEST_SCORE));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Karma", player) + user.getStat(StatsStorage.StatisticType.KARMA));
        sender.sendMessage(ChatManager.colorMessage("Commands.Stats-Command.Footer", player));
      }
    });
  }

}
