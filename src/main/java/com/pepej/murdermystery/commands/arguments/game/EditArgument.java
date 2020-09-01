package com.pepej.murdermystery.commands.arguments.game;

import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.commands.completion.CompletableArgument;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.handlers.gui.setup.SetupInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class EditArgument {

    public EditArgument(ArgumentsRegistry registry) {
        registry.mapArgument("murdermystery", new CommandArgument("edit", "murdermystery.admin.edit", CommandArgument.ExecutorType.PLAYER) {

            @Override
            public void execute(CommandSender sender, String[] args) {
                if (args.length == 1) {
                    Player player = (Player) sender;
                    ChatManager.sendMessage(player, "&6Укажите ID арены");
                    return;
                }
                if (ArenaRegistry.getArena(args[1]) == null) {
                    sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Arena-Like-That"));
                    return;
                }

                new SetupInventory(ArenaRegistry.getArena(args[1]), (Player) sender).openInventory();

            }

        });

        registry.getTabCompletion().registerCompletion(new CompletableArgument("murdermystery", "edit", ArenaRegistry.getArenas().stream().map(Arena::getId).collect(Collectors.toList())));
    }
}
