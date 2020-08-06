package pl.plajer.murdermystery.perks;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.plajer.murdermystery.arena.Arena;

import java.util.ArrayList;
import java.util.List;


public abstract class Perk {

    @Getter
    @NotNull
    private Integer id;

    @Getter
    @NotNull
    private String name;

    @Getter
    @NotNull
    private static List<Perk> allPerks = new ArrayList<>();

    @Getter
    private List<ItemStack> items;

    @Getter
    private List<PotionEffect> effects;

    @Getter
    @NotNull
    private ItemStack displayItem;

    @NonNull
    @Getter
    private Double price;

    public static void init() {
        new SpeedPerk();
        new ExtremeGoldPerk();
        new UdavkaNahuyPerk();
    }


    protected Perk(@NotNull Integer id, @NotNull String name, @NonNull Double price, @NotNull ItemStack displayItem, @Nullable List<ItemStack> items, @Nullable List<PotionEffect> effects) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.displayItem = displayItem;
        this.items = items;
        this.effects = effects;
        allPerks.add(this);
    }

    public void buy(Player player, int price) {
        //TODO not implemented yet
    }

    public boolean has(Player player) {
        //TODO not implemented yet
        return true;
    }

    public static List<Perk> getPerks(Player player) {
        return null;
    }

    public abstract void get(Player player, Player target, Arena arena);


}
