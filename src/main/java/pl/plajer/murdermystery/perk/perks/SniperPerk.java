package pl.plajer.murdermystery.perk.perks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.perk.Perk;
import pl.plajer.murdermystery.perk.PerkAnn;

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
