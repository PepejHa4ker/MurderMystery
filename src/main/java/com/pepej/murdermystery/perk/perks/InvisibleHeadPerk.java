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
        if (random < 2) {
            ChatManager.sendMessage(player, "&eВжух! Исчезни, если не петух");
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
