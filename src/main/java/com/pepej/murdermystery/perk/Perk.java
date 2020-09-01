package com.pepej.murdermystery.perk;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.economy.PriceType;
import com.pepej.murdermystery.handlers.ChatManager;
import lombok.Getter;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public abstract class Perk implements Comparable<Perk> {

    @Getter
    @NotNull
    private final String name;

    @Getter
    @NotNull
    private static final List<Perk> cachedPerks = new ArrayList<>();

    @Getter
    @NotNull
    private final List<String> description;

    @Getter
    @NotNull
    private final Material displayItem;

    @Getter
    @NotNull
    private final Double cost;

    public Perk(@NotNull String name, @NotNull Double cost, @NotNull Material displayItem, @NotNull String... description) {
        this.name = name;
        this.cost = cost;
        this.displayItem = displayItem;
        this.description = Arrays.stream(description).map(ChatManager::colorRawMessage).collect(Collectors.toList());
    }

    public boolean success() {
        return true;
    }

    public void tryBuy(Player player, PriceType type) {
        int adaptedCost = (int) (type == PriceType.COINS ? this.cost : (this.cost / 10));
        val user = MurderMystery.getInstance().getUserManager().getUser(player);
        if (user.getCachedPerks().contains(this)) {
            ChatManager.sendMessage(player, "&cСпособность уже выбрана");
            return;
        }
        if (type == PriceType.COINS) {
            if (MurderMystery.getInstance().getEconomy().getBalance(user.getPlayer()) < adaptedCost) {
                ChatManager.sendMessage(player, "&cНедостаточно средств");
                return;
            }
        } else {
            if (user.getStat(StatsStorage.StatisticType.KARMA) < adaptedCost) {
                ChatManager.sendMessage(player, "&cУ Вас плохая карма :(");
                return;
            }
        }

        if (player.hasPermission("murder.prem")) {
            if (user.getCachedPerks().size() == 2) {
                ChatManager.sendMessage(player, "&cВы можете взять только 2 способности на игру");
                return;
            }
        } else {
            if (user.getCachedPerks().size() == 1) {
                ChatManager.sendMessage(player, "&cВы можете взять только 1 способность на игру");
                return;
            }
        }
        if (type == PriceType.COINS) {
            MurderMystery.getInstance().getEconomy().withdrawPlayer(user.getPlayer(), this.getCost());
        } else
            user.setStat(StatsStorage.StatisticType.KARMA, user.getStat(StatsStorage.StatisticType.KARMA) - adaptedCost);
        user.getCachedPerks().add(this);
        ChatManager.sendMessage(player, "&6Способность " + this.getName() + " &6успешно выбрана");
    }


    public static Perk get(Class<? extends Perk> clazz) {
        for (Perk perk : PerkRegister.getCachedPerks()) {
            if (perk.getClass().equals(clazz)) {
                return perk;
            }
        }
       throw new IllegalArgumentException("Cannot find perk");
    }

    public static boolean has(Player player, Class<? extends Perk> clazz) {
        for (Perk perk : MurderMystery.getInstance().getUserManager().getUser(player).getCachedPerks()) {
            if (perk.getClass().equals(clazz)) {
                return true;
            }
        }
        return false;
    }


    public abstract void handle(@NotNull final Player player, Player target, final @NotNull Arena arena);

    @Override
    public int compareTo(@NotNull Perk perk) {
        //Comparing by cost
        if (this.cost.equals(perk.cost)) {
            return 1;
        }
        if (this.cost > perk.cost) {
            return 1;
        } else {
            return -1;
        }
    }
}
