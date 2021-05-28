package com.pepej.murdermystery.perk.perks;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.perk.Perk;
import com.pepej.murdermystery.perk.PerkAnn;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@PerkAnn
public class SniperPerk extends Perk {

    public SniperPerk() {
        super(
                "&aСнайпер",
                50.0,
                Material.BOW,
                "&eЗа убийство &cманьяка &eвы получите &a75 монет &eдополнительно"
        );
    }

    @Override
    public void handle(@NotNull final Player player, @Nullable Player target, @NotNull final Arena arena) {
        ChatManager.sendMessage(player, "&eВы получили &a75 &eмонет за счёт способности \"&aСнайпер&e\"");
        MurderMystery.getInstance().getEconomy().depositPlayer(player, 75.0);
    }
}
