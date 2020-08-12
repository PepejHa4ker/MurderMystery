package pl.plajer.murdermystery.perks;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.effects.PotionEffectBuilder;
import pl.plajer.murdermystery.utils.effects.particle.ParticlePlayer;
import pl.plajer.murdermystery.utils.effects.particle.effect.SpiralEffect;
import pl.plajer.murdermystery.utils.items.ItemBuilder;


public class SpeedPerk extends Perk {

    protected SpeedPerk() {
        super(
                "§bБеги, пока можешь",
                200.0,
                new ItemBuilder(Material.RABBIT_FOOT)
                        .name("&bБеги, пока можешь")
                        .lore("&eВы можете с некоторым шансом получить скорость")
                        .lore("&eЦена: &c200.0&e монет")
                        .build(),
                null);


    }

    @Override
    public synchronized void handle(Player player, Player target, Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 5) {
            player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.SPEED)
            .setVisible(false)
            .setDuration(10)
            .setAmbient(true)
            .setAmplifier(0)
            .build());
            val effect = new SpiralEffect(Main.getInstance().getScheduledExecutorService(),
                    player.getLocation(),
                    new ParticlePlayer(Particle.SLIME),
                    20,
                    2,
                    2,
                    15,
                    0.75,
                    true,
                    5
            ).play();

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), effect::stop, 20);
        }
    }
}

