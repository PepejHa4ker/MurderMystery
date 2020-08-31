

package pl.plajer.murdermystery.commands.arguments.admin.arena;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaManager;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.commands.arguments.ArgumentsRegistry;
import pl.plajer.murdermystery.commands.arguments.data.CommandArgument;
import pl.plajer.murdermystery.commands.arguments.data.LabelData;
import pl.plajer.murdermystery.commands.arguments.data.LabeledCommandArgument;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.utils.config.ConfigUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Plajer
 * <p>
 * Created at 18.05.2019
 */
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class DeleteArgument {

  private Set<CommandSender> confirmations = new HashSet<>();

  public DeleteArgument(ArgumentsRegistry registry) {
    registry.mapArgument("murdermysteryadmin", new LabeledCommandArgument("delete", "murdermystery.admin.delete", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/mma delete &6<arena>", "/mma delete <arena>",
        "&7Deletes specified arena\n&6Permission: &7murdermystery.admin.delete")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Type-Arena-Name"));
          return;
        }
        Arena arena = ArenaRegistry.getArena(args[1]);
        if (arena == null) {
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
          return;
        }
        if (!confirmations.contains(sender)) {
          confirmations.add(sender);
          Bukkit.getScheduler().runTaskLater(registry.getPlugin(), () -> confirmations.remove(sender), 20 * 10);
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorRawMessage("&cAre you sure you want to do this action? Type the command again &6within 10 seconds &cto confirm!"));
          return;
        }
        confirmations.remove(sender);
        ArenaManager.stopGame(true, arena);
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "arenas");

        config.set("instances." + args[1], null);
        ConfigUtils.saveConfig(registry.getPlugin(), config, "arenas");
        ArenaRegistry.unregisterArena(arena);
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Removed-Game-Instance"));
      }
    });
  }

}
