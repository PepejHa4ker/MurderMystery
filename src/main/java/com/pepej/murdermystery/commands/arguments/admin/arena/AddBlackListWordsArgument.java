package com.pepej.murdermystery.commands.arguments.admin.arena;

import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.commands.arguments.data.LabelData;
import com.pepej.murdermystery.commands.arguments.data.LabeledCommandArgument;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;


public class AddBlackListWordsArgument {


    public AddBlackListWordsArgument(ArgumentsRegistry registry) {
        FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "filter");
        registry.mapArgument("murdermysteryadmin", new LabeledCommandArgument("add", "murdermystery.admin.add", CommandArgument.ExecutorType.BOTH,
                new LabelData("/mma add &6<word>", "/mma add <word>",
                        "&7Добавить слова в черный список, слова можно указывать через пробел\n&6Permission: &7murdermystery.admin.add")) {
            @Override
            public void execute(CommandSender sender, String[] args) {
                List<String> wordsList = config.getStringList("words");
                if (args.length < 2) {
                    sender.sendMessage("§cНедостаточно аргументов. Укажите слова");
                    return;
                }
                List<String> toAdd =  Arrays.asList(args);
                toAdd.remove(0);
                wordsList.addAll(toAdd);
                config.set("words", wordsList);
                sender.sendMessage("§cУспешно");
                ConfigUtils.saveConfig(registry.getPlugin(), config, "filter");
            }
        });

    }
}


