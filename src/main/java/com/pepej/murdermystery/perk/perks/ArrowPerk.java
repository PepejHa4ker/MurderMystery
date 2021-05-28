package com.pepej.murdermystery.perk.perks;

import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.role.Role;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.perk.Perk;
import com.pepej.murdermystery.perk.PerkAnn;
import com.pepej.murdermystery.utils.Utils;
import com.pepej.murdermystery.utils.items.ItemPosition;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
