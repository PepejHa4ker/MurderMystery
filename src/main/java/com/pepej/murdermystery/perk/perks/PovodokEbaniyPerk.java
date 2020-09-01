package com.pepej.murdermystery.perk.perks;

import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.perk.Perk;
import com.pepej.murdermystery.perk.PerkAnn;
import com.pepej.murdermystery.utils.items.ItemPosition;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

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
