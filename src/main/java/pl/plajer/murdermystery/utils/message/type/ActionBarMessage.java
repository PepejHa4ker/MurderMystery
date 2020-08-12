package pl.plajer.murdermystery.utils.message.type;

import lombok.Getter;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.utils.message.NMSClasses;

public class ActionBarMessage implements IMessage {

    @Getter
    private final String actionBar;

    /**
     * Create new ActionBarMessage
     *
     * @param actionBar The message
     */
    public ActionBarMessage(String actionBar) {
        this.actionBar = actionBar;
    }

    /**
     * Send message to player
     *
     * @param player The player
     */
    @Override
    public void send(Player player) {
        NMSClasses.sendActionBarNMS(player, actionBar);
    }



}
