package pl.plajer.murdermystery.perks;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.plajer.murdermystery.Main;
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
        new InvisibleHeadPerk();
        new SecondChancePerk();
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

    public boolean buy(Player player) {
        val user = Main.getInstance().getUserManager().getUser(player);
        if (user.getPerks().contains(this)) {
            user.getPlayer().sendMessage("§cПерк уже выбран");
            return false;
        }
        if (Main.getInstance().getEconomy().getBalance(user.getPlayer()) < this.getPrice()) {
            user.getPlayer().sendMessage("§cНедостаточно средств.");
            return false;
        }

        if (user.getPerks().size() == 1) {
            user.getPlayer().sendMessage("§cВы можете взять только 1 способность на игру.");
            return false;
        }

        Main.getInstance().getEconomy().withdrawPlayer(user.getPlayer(), this.getPrice());
        user.getPerks().add(this);
        player.sendMessage("§6Перк " + this.getName() + " §6успешно выбран.");
        return true;
    }

    public static Perk getPerkByName(String perkName) {
        for(Perk perk : getAllPerks()) {
            if(perk.getName().equalsIgnoreCase(perkName)) {
                return perk;
            }
        }
        return null;
    }

    public static Perk getPerkById(Integer id) {
        for(Perk perk : getAllPerks()) {
            if(perk.getId().equals(id)) {
                return perk;
            }
        }
        return null;
    }

    public static Perk getPerkByClass(Class<? extends Perk> clazz) {
        for(Perk perk : getAllPerks()) {
            if(perk.getClass() == clazz) {
                return perk;
            }
        }
        return null;
    }

    public static boolean has(Player player, Class<? extends Perk> clazz) {
        for(Perk perk : Main.getInstance().getUserManager().getUser(player).getPerks()) {
            if(perk.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }



    public abstract void handle(Player player, Player target, Arena arena);


}
