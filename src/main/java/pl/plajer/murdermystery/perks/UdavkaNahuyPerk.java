package pl.plajer.murdermystery.perks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.utils.effects.PotionEffectBuilder;
import pl.plajer.murdermystery.utils.items.ItemBuilder;
import pl.plajer.murdermystery.utils.items.ItemPosition;

public class UdavkaNahuyPerk extends Perk {

    public static ItemStack item = new ItemBuilder(Material.LEASH)
            .name("&8Заключённый")
            .lore("&eПри использовании этой способности")
            .lore("&eвы можете дать другому игроку негативные эффекты")
            .lore("&eЦена: &c600.0&e монет")
            .build();


    protected UdavkaNahuyPerk() {
        super(
                "§8Заключённый",
                600.0,
                item,
                null);
    }

    @Override
    public void handle(Player player, Player target, Arena arena) {
        player.addPotionEffect(
                new PotionEffectBuilder(PotionEffectType.SLOW)
                        .setDuration(10)
                        .setAmplifier(0)
                        .setVisible(false)
                        .build()
        );
        player.addPotionEffect(
                new PotionEffectBuilder(PotionEffectType.BLINDNESS)
                        .setDuration(10)
                        .setAmplifier(0)
                        .setVisible(false)
                        .build()
        );

        ItemPosition.setItem(player, ItemPosition.UDAVKA, null);

    }
}
