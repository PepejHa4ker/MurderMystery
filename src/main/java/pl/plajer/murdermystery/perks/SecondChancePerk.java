package pl.plajer.murdermystery.perks;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import java.util.Random;

public class SecondChancePerk extends Perk {

    protected SecondChancePerk() {
        super(
                4,
                "§cВторой шанс",
                500.0,
                new ItemBuilder(Material.BED)
                .name("§cВторой шанс")
                .lore("§eС шансом 50% Вы можете получить второй шанс после смерти")
                .build(),
                null,
                null);
    }

    @Override
    public void handle(Player player, Player target, Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if(random < 50) {
            player.teleport(arena.getPlayerSpawnPoints().get(new Random().nextInt(arena.getPlayerSpawnPoints().size())));
        }
    }
}
