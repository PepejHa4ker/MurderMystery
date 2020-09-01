package com.pepej.murdermystery.utils.message.type;

import com.pepej.murdermystery.utils.message.NMSClasses;
import lombok.Getter;
import org.bukkit.entity.Player;

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
