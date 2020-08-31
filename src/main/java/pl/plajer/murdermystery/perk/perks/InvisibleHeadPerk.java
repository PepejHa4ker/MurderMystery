package pl.plajer.murdermystery.perk.perks;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.perk.Perk;
import pl.plajer.murdermystery.perk.PerkAnn;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.effects.EffectUtils;
import pl.plajer.murdermystery.utils.effects.PotionEffectBuilder;
import pl.plajer.murdermystery.utils.effects.particle.ParticlePlayer;
import pl.plajer.murdermystery.utils.effects.particle.effect.SpiralEffect;

@PerkAnn
public class InvisibleHeadPerk extends Perk {

    public InvisibleHeadPerk() {
        super(
                "&dМагическая шляпа",
                750.0,
                Material.GOLD_HELMET,
                "&eС помощью этой способности Вы сможете",
                "&eс маленьким шансом получить невидимость в течение игры."
        );
    }


    @Override
    public void handle(final @NotNull Player player, Player target, final @NotNull Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 3) {
            val effect = new SpiralEffect(MurderMystery.getInstance().getScheduledExecutorService(),
                    player.getLocation(),
                    new ParticlePlayer(Particle.FLAME),
                    3,
                    2,
                    10,
                    15,
                    1,
                    true,
                    5
            ).play();
            Bukkit.getScheduler().runTaskLater(MurderMystery.getInstance(), effect::stop, 10);
            EffectUtils.addEffect(player, new PotionEffectBuilder(PotionEffectType.INVISIBILITY).setDuration(10).setAmplifier(0).setAmbient(true).setVisible(false).build());

        }
    }
}
