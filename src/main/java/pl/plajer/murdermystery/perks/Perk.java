package pl.plajer.murdermystery.perks;

import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;


public abstract class Perk {

    @Getter
    @NotNull
    private final String name;

    @Getter
    @NotNull
    private static final List<Perk> allPerks = new ArrayList<>();

    @Getter
    @NotNull
    private final ItemStack displayItem;

    @NonNull
    @Getter
    private final Double price;

    @Getter
    private final ScheduledExecutorService scheduler;

    public static void init() {
        new SpeedPerk();
        new ExtremeGoldPerk();
        new UdavkaNahuyPerk();
        new InvisibleHeadPerk();
        new SecondChancePerk();
    }

    protected Perk(@NotNull String name, @NonNull Double price, @NotNull ItemStack displayItem, ScheduledExecutorService scheduler) {
        this.name = name;
        this.price = price;
        this.displayItem = displayItem;
        this.scheduler = scheduler;
        allPerks.add(this);
    }

    public boolean success() {
        return true;
    }

    public void tryBuy(Player player) {
        val user = MurderMystery.getInstance().getUserManager().getUser(player);
        if (user.getPerks().contains(this)) {
            user.getPlayer().sendMessage("§cСпособность уже выбрана");
            return;
        }
        if (MurderMystery.getInstance().getEconomy().getBalance(user.getPlayer()) < this.getPrice()) {
            user.getPlayer().sendMessage("§cНедостаточно средств");
            return;
        }

        if (player.hasPermission("murder.prem")) {
            if (user.getPerks().size() == 2) {
                user.getPlayer().sendMessage("§cВы можете взять только 2 способности на игру");
                return;
            }
        } else {
            if (user.getPerks().size() == 1) {
                user.getPlayer().sendMessage("§cВы можете взять только 1 способность на игру");
                return;
            }
        }

        MurderMystery.getInstance().getEconomy().withdrawPlayer(user.getPlayer(), this.getPrice());
        user.getPerks().add(this);
        player.sendMessage("§6Способность " + this.getName() + " §6успешно выбрана");
    }

    public static Perk getPerkByName(String perkName) {
        for (Perk perk : getAllPerks()) {
            if (perk.getName().equalsIgnoreCase(perkName)) {
                return perk;
            }
        }
        throw new IllegalArgumentException("Cannot find perk");
    }


    public static Perk getPerkByClass(Class<? extends Perk> clazz) {
        for (Perk perk : getAllPerks()) {
            if (perk.getClass() == clazz) {
                return perk;
            }
        }
       throw new IllegalArgumentException("Cannot find perk");
    }

    public static boolean has(Player player, Class<? extends Perk> clazz) {
        for (Perk perk : MurderMystery.getInstance().getUserManager().getUser(player).getPerks()) {
            if (perk.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }


    public abstract void handle(Player player, Player target, Arena arena);


}
