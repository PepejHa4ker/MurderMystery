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

public class UdavkaNahuyPerk extends Perk {

    public static ItemStack item = new ItemBuilder(Material.LEASH)
            .name("§8Заключённый")
            .lore("§eПри использовании этой способности")
            .lore("§eвы можете дать другому игроку негативные эффекты")
            .lore("§eЦена: §c600.0§e монет")
            .build();

    protected UdavkaNahuyPerk() {
        super(
                2,
                "Заключённый",
                600.0,
                item,
                null,
                Arrays.asList(
                        new PotionEffect(PotionEffectType.BLINDNESS, 10*20, 0),
                        new PotionEffect(PotionEffectType.SLOW, 10*20, 0)
                ));
    }

    @Override
    public void get(Player player, Player target, Arena arena) {
        this.getEffects().forEach(target::addPotionEffect);
        ItemPosition.setItem(player, ItemPosition.UDAVKA, null);

    }
}
