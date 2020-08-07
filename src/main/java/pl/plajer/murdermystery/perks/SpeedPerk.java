package pl.plajer.murdermystery.perks;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import java.util.Collections;


public class SpeedPerk extends Perk {

    protected SpeedPerk() {
        super(
                0,
                "§bБеги, пока можешь",
                200.0,
                new ItemBuilder(Material.RABBIT_FOOT)
                        .name("§bБеги, пока можешь")
                        .lore("§eВы можете с некоторым шансом получить скорость")
                        .lore("§eЦена: §c200.0§e монет")
                        .build(),
                null,
                Collections.singletonList(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 0, true, false))
        );
    }

    @Override
    public void handle(Player player, Player target, Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 5) {
            this.getEffects().forEach(player::addPotionEffect);
        }
    }
}

