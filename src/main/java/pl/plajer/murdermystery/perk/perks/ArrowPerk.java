package pl.plajer.murdermystery.perk.perks;

import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.role.Role;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.perk.Perk;
import pl.plajer.murdermystery.perk.PerkAnn;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.items.ItemPosition;

@PerkAnn
public class ArrowPerk extends Perk {

    public ArrowPerk() {
        super(
                "&9Колчан",
                500.0,
                Material.ARROW,
                "&eКаждую секунду у Вас есть &c3% &eшанс получить стрелу"

        );

    }

    @Override
    public void handle(final @NotNull Player player, Player target, final @NotNull Arena arena) {
        val random = Utils.getRandomNumber(0, 100);
        if (random < 3) {
            if (Role.isRole(Role.ANY_DETECTIVE, player)) {
                ChatManager.sendMessage(player, "&c+1 &6стрела");
                ItemPosition.addItem(player, ItemPosition.ARROWS, new ItemStack(Material.ARROW));
            }

        }
    }
}
