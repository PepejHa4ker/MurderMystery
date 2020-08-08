package pl.plajer.murdermystery.perks;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.particle.ParticlePlayer;
import pl.plajer.murdermystery.utils.particle.effect.SpiralEffect;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

;

public class InvisibleHeadPerk extends Perk {


    protected InvisibleHeadPerk() {
        super(
                "§5Магическая шляпа",
                750.0,
                new ItemBuilder(Material.IRON_HELMET)
                        .name("§5Магическая шляпа")
                        .lore("§eС помощью этой способности Вы сможете")
                        .lore("§eс маленьким шансом получить невидимость в течение игры.")
                        .lore("§eЦена: §c750.0§e монет")
                        .enchantment(Enchantment.PROTECTION_ENVIRONMENTAL)
                        .build(),
                null
        );
    }


    @Override
    public synchronized void handle(Player player, Player target, Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 3) {
            val effect = new SpiralEffect(Main.getInstance().getScheduledExecutorService(),
                    player.getLocation(),
                    new ParticlePlayer(Particle.FLAME),
                    3,
                    2,
                    10,
                    10,
                    1,
                    true,
                    3
            ).play();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), effect::stop, 50);

            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10 * 20, 0, true, true));
        }
    }
}
