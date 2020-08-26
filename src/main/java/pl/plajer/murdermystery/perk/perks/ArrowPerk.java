package pl.plajer.murdermystery.perk.perks;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.role.Role;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.perk.Perk;
import pl.plajer.murdermystery.perk.PerkAnn;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.items.ItemBuilder;
import pl.plajer.murdermystery.utils.items.ItemPosition;

@PerkAnn
public class ArrowPerk extends Perk {

    public ArrowPerk() {
        super(
                "§9Колчан",
                500.0,
                new ItemBuilder(Material.ARROW)
                        .name("&9Колчан")
                        .lore("&eКаждую секунду у Вас есть &c3% &eшанс получить стрелу")
                        .lore("&eЦена: &c500.0&e монет")
                        .build()
        );

    }

    @Override
    public void handle(Player player, Player target, Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 3) {
            if (Role.isRole(Role.ANY_DETECTIVE, player)) {
                ChatManager.sendMessage(player, "&c+1 &6стрела");
                ItemPosition.addItem(player, ItemPosition.ARROWS, new ItemStack(Material.ARROW));
            }

        }
    }
}
