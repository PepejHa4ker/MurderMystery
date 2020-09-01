package com.pepej.murdermystery.commands.completion;

import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class TabCompletion implements TabCompleter {

  List<CompletableArgument> registeredCompletions = new ArrayList<>();
  ArgumentsRegistry registry;

  public TabCompletion(ArgumentsRegistry registry) {
    this.registry = registry;
  }

  public void registerCompletion(CompletableArgument completion) {
    registeredCompletions.add(completion);
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return Collections.emptyList();
    }
    if (cmd.getName().equalsIgnoreCase("murdermysteryadmin") && args.length == 1) {
      return registry.getMappedArguments().get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName).collect(Collectors.toList());
    }
    if (cmd.getName().equalsIgnoreCase("murdermystery")) {
      if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
        List<String> arenaIds = new ArrayList<>();
        for (Arena arena : ArenaRegistry.getArenas()) {
          arenaIds.add(arena.getId());
        }
        return arenaIds;
      }
      if (args.length == 1) {
        return registry.getMappedArguments().get(cmd.getName().toLowerCase()).stream().map(CommandArgument::getArgumentName).collect(Collectors.toList());
      }
    }
    if (args.length < 2) {
      return Collections.emptyList();
    }
    for (CompletableArgument completion : registeredCompletions) {
      if (!cmd.getName().equalsIgnoreCase(completion.getMainCommand()) || !completion.getArgument().equalsIgnoreCase(args[0])) {
        continue;
      }
      return completion.getCompletions();
    }
    return Collections.emptyList();
  }
}
