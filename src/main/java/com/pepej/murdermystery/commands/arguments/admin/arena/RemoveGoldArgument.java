package com.pepej.murdermystery.commands.arguments.admin.arena;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.commands.arguments.data.LabelData;
import com.pepej.murdermystery.commands.arguments.data.LabeledCommandArgument;
import com.pepej.murdermystery.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveGoldArgument {


    public RemoveGoldArgument(ArgumentsRegistry registry) {
        registry.mapArgument("murdermysteryadmin", new LabeledCommandArgument("removegold", "murdermystery.admin.removegold", CommandArgument.ExecutorType.PLAYER,
                new LabelData("/mma removegold", "/mma removegold", "")) {
            @Override
            public void execute(CommandSender sender, String[] args) {
                if (args.length < 3) return;
                String player = args[1];
                if (Bukkit.getPlayer(player) == null) return;
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException ignored) {
                    return;
                }
                Player p = Bukkit.getPlayer(player);
                User u = MurderMystery.getInstance().getUserManager().getUser(p);
                if (ArenaRegistry.getArena(p) == null) return;
                if (u.getStat(StatsStorage.StatisticType.LOCAL_GOLD) < amount) return;
                u.setStat(StatsStorage.StatisticType.LOCAL_GOLD, (u.getStat(StatsStorage.StatisticType.LOCAL_GOLD) - amount));
            }
        });

    }
}
