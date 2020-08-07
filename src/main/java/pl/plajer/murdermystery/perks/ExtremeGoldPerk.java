package pl.plajer.murdermystery.perks;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.utils.ItemPosition;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import java.util.Collections;

public class ExtremeGoldPerk extends Perk {

    protected ExtremeGoldPerk() {
        super(
                1,
                "§eЗолотая лихорадка",
                500.0,
                new ItemBuilder(Material.GOLD_INGOT)
                        .name("§6Золотая лихорадка")
                        .lore("§eУ Вас есть небольшой шанс")
                        .lore("§eполучить дополнительный слиток золота")
                        .lore("§7(есть побочные эффекты)")
                        .lore("§eЦена: §c500.0§e монет")
                        .build(),
                Collections.singletonList(new ItemStack(Material.GOLD_INGOT)),
                Collections.singletonList(new PotionEffect(PotionEffectType.BLINDNESS, 10 * 20, 0)));
    }

    @Override
    public void handle(Player player, Player target, Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 30) {
            ItemPosition.addItem(player, ItemPosition.GOLD_INGOTS, this.getItems().get(0));
            Main.getInstance().getUserManager().getUser(player).addStat(StatsStorage.StatisticType.LOCAL_GOLD, 1);
            this.getEffects().forEach(player::addPotionEffect);
        }
    }
}
