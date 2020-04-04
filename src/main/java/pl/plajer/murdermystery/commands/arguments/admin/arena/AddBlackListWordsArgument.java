package pl.plajer.murdermystery.commands.arguments.admin.arena;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.murdermystery.commands.arguments.ArgumentsRegistry;
import pl.plajer.murdermystery.commands.arguments.data.CommandArgument;
import pl.plajer.murdermystery.commands.arguments.data.LabelData;
import pl.plajer.murdermystery.commands.arguments.data.LabeledCommandArgument;
import pl.plajerlair.commonsbox.minecraft.configuration.ConfigUtils;

import java.util.List;


public class AddBlackListWordsArgument {


    public AddBlackListWordsArgument(ArgumentsRegistry registry) {
        registry.mapArgument("murdermysteryadmin", new LabeledCommandArgument("add", "murdermystery.admin.add", CommandArgument.ExecutorType.BOTH,
                new LabelData("/mma add &6<word>", "/mma add <word>",
                        "&7Add specified words\n&6Permission: &7murdermystery.admin.add")) {
            @Override
            public void execute(CommandSender sender, String[] args) {
                FileConfiguration config = ConfigUtils.getConfig(registry.getPlugin(), "filter");

                List<String> words = config.getStringList("words");
                if(args.length < 2) {
                    sender.sendMessage("§cНедостаточно аргументов. Укажите слово");
                    return;
                }
                words.add(args[1]);
                config.set("words", words);
                sender.sendMessage("§cУспешно");
                ConfigUtils.saveConfig(registry.getPlugin(), config, "filter");
            }
        });
    }
}


