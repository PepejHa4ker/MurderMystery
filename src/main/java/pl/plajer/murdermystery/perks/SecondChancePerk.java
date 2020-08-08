package pl.plajer.murdermystery.perks;

import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.particle.ParticlePlayer;
import pl.plajer.murdermystery.utils.particle.effect.SpiralEffect;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import java.util.Random;

public class SecondChancePerk extends Perk {

    private boolean success;

    protected SecondChancePerk() {
        super(
                "§cВторой шанс",
                500.0,
                new ItemBuilder(Material.BED)
                        .name("§cВторой шанс")
                        .lore("§eС шансом 50% Вы можете получить второй шанс после смерти")
                        .lore("§7(работает только на удары мечём)")
                        .lore("§eЦена: §c500.0§e монет")
                        .build(),
                null
        );
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public void handle(Player player, Player target, Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 50) {
            val effect = new SpiralEffect(Main.getInstance().getScheduledExecutorService(),
                    player.getLocation(),
                    new ParticlePlayer(Particle.CRIT),
                    20,
                    2,
                    2,
                    15,
                    0.75,
                    true,
                    5
            ).play();
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), effect::stop, 20);
            player.teleport(arena.getPlayerSpawnPoints().get(new Random().nextInt(arena.getPlayerSpawnPoints().size())));
            player.sendMessage("§cВот же повезло удрать!");
            success = true;
        } else {
            success = false;
        }
    }
}
