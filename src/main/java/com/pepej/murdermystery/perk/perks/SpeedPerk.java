package com.pepej.murdermystery.perk.perks;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.perk.Perk;
import com.pepej.murdermystery.perk.PerkAnn;
import com.pepej.murdermystery.utils.Utils;
import com.pepej.murdermystery.utils.effects.EffectUtils;
import com.pepej.murdermystery.utils.effects.PotionEffectBuilder;
import com.pepej.murdermystery.utils.effects.particle.ParticlePlayer;
import com.pepej.murdermystery.utils.effects.particle.effect.SpiralEffect;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

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

