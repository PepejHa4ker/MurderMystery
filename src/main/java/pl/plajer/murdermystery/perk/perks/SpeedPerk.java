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
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.perk.Perk;
import pl.plajer.murdermystery.perk.PerkAnn;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.effects.EffectUtils;
import pl.plajer.murdermystery.utils.effects.PotionEffectBuilder;
import pl.plajer.murdermystery.utils.effects.particle.ParticlePlayer;
import pl.plajer.murdermystery.utils.effects.particle.effect.SpiralEffect;

@PerkAnn
public class SpeedPerk extends Perk {

    public SpeedPerk() {
        super(
                "&bБеги, пока можешь",
                200.0,
                Material.RABBIT_FOOT,
                "&eВы можете с некоторым шансом получить скорость"
        );
    }

    @Override
    public void handle(final @NotNull Player player, Player target, final @NotNull Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 3) {
            ChatManager.sendMessage(player, "&bЯ чувствую скорость... Я ФЛЭШ");
            EffectUtils.addEffect(player, new PotionEffectBuilder(PotionEffectType.SPEED)
                    .setDuration(10)
                    .setAmplifier(0)
                    .setAmbient(true)
                    .setVisible(false)
                    .build());
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

