package com.pepej.murdermystery.perk.perks;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.perk.Perk;
import com.pepej.murdermystery.perk.PerkAnn;
import com.pepej.murdermystery.utils.Utils;
import com.pepej.murdermystery.utils.effects.particle.ParticlePlayer;
import com.pepej.murdermystery.utils.effects.particle.effect.SpiralEffect;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@PerkAnn
public class SecondChancePerk extends Perk {

    private boolean success;

    public SecondChancePerk() {
        super(
                "&cВторой шанс",
                500.0,
                Material.BED,
                "&eС шансом 25% Вы можете получить второй шанс после смерти",
                "&7(работает только на удары мечем и выстрелы)"

        );
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public void handle(final @NotNull Player player, Player target, final @NotNull Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 25) {
            val effect = new SpiralEffect(MurderMystery.getInstance().getScheduledExecutorService(),
                    player.getLocation(),
                    new ParticlePlayer(Particle.CRIT_MAGIC),
                    3,
                    2,
                    10,
                    10,
                    1,
                    true,
                    3
            ).play();
            Bukkit.getScheduler().runTaskLater(MurderMystery.getInstance(), effect::stop, 20);
            player.teleport(arena.getPlayerSpawnPoints().get(new Random().nextInt(arena.getPlayerSpawnPoints().size())));
            ChatManager.sendMessage(player, "&cВот же повезло удрать!");
            success = true;
        } else {
            success = false;
        }
    }
}
