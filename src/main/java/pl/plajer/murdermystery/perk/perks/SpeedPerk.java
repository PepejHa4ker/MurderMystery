package pl.plajer.murdermystery.perk.perks;

import lombok.NonNull;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.perk.Perk;
import pl.plajer.murdermystery.perk.PerkAnn;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.effects.EffectUtils;
import pl.plajer.murdermystery.utils.effects.PotionEffectBuilder;
import pl.plajer.murdermystery.utils.effects.particle.ParticlePlayer;
import pl.plajer.murdermystery.utils.effects.particle.effect.SpiralEffect;
import pl.plajer.murdermystery.utils.items.ItemBuilder;

@PerkAnn
public class SpeedPerk extends Perk {

    public SpeedPerk() {
        super(
                "§bБеги, пока можешь",
                200.0,
                new ItemBuilder(Material.RABBIT_FOOT)
                        .name("&bБеги, пока можешь")
                        .lore("&eВы можете с некоторым шансом получить скорость")
                        .lore("&eЦена: &c200.0&e монет")
                        .build()
                );


    }

    @Override
    public void handle(@NonNull final Player player, Player target, @NonNull final Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 5) {
            EffectUtils.addEffect(player, new PotionEffectBuilder(PotionEffectType.SPEED).setDuration(10).setAmplifier(0).setAmbient(true).setVisible(false).build());
            val effect = new SpiralEffect(MurderMystery.getInstance().getScheduledExecutorService(),
                    player.getLocation(),
                    new ParticlePlayer(Particle.END_ROD),
                    20,
                    2,
                    2,
                    15,
                    0.75,
                    true,
                    5
            ).play();

            Bukkit.getScheduler().runTaskLater(MurderMystery.getInstance(), effect::stop, 20);
        }
    }
}

