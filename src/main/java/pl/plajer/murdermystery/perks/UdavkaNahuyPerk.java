package pl.plajer.murdermystery.perks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.utils.ItemPosition;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import java.util.Arrays;
import java.util.List;

public class UdavkaNahuyPerk extends Perk {

    public static ItemStack item = new ItemBuilder(Material.LEASH)
            .name("§8Заключённый")
            .lore("§eПри использовании этой способности")
            .lore("§eвы можете дать другому игроку негативные эффекты")
            .lore("§eЦена: §c600.0§e монет")
            .build();

    private List<PotionEffect> effects =  Arrays.asList(
            new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 0),
            new PotionEffect(PotionEffectType.SLOW, 10*20, 0)
    );


    protected UdavkaNahuyPerk() {
        super(
                "§8Заключённый",
                600.0,
                item,
                null);
    }

    @Override
    public void handle(Player player, Player target, Arena arena) {
        this.effects.forEach(target::addPotionEffect);
        ItemPosition.setItem(player, ItemPosition.UDAVKA, null);

    }
}
