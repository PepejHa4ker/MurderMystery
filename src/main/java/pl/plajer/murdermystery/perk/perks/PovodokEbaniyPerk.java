package pl.plajer.murdermystery.perk.perks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.perk.Perk;
import pl.plajer.murdermystery.perk.PerkAnn;
import pl.plajer.murdermystery.utils.items.ItemPosition;

@PerkAnn
public class PovodokEbaniyPerk extends Perk {

    public PovodokEbaniyPerk() {
        super(
                "&8Заключённый",
                600.0,
                Material.LEASH,
                "&eПри использовании этой способности",
                "&eвы можете дать другому игроку негативные эффекты"
        );
    }

    @Override
    public void handle(final @NotNull Player player, Player target, final @NotNull Arena arena) {
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 10 * 20, 0, false, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0, false, false));
        ItemPosition.setItem(player, ItemPosition.UDAVKA, null);

    }
}
