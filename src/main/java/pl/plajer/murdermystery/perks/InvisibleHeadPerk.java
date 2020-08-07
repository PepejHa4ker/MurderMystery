package pl.plajer.murdermystery.perks;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import java.util.Collections;

;

public class InvisibleHeadPerk extends Perk {

    private static ItemStack item = new ItemBuilder(Material.IRON_HELMET)
            .name("§5Магическая шляпа")
            .lore("§eС помощью этой способности Вы сможете")
            .lore("§eс маленьким шансом получить невидимость в течение игры.")
            .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL)
            .build();

    protected InvisibleHeadPerk() {
        super(
                3,
                "§5Магическая шляпа",
                750.0,
                item,
                Collections.singletonList(item),
                Collections.singletonList(new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 0, true, true)));
    }

    @Override
    public void handle(Player player, Player target, Arena arena) {
            val random = Utils.getRandomNumber(0, 100);
            if (random < 3) {
                getEffects().forEach(player::addPotionEffect);
        }

    }
}
