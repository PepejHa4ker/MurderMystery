package pl.plajer.murdermystery.utils.message.type;

import org.bukkit.entity.Player;

public interface IMessage {

    /**
     * Send message to player
     *
     * @param player The player
     */
    void send(Player player);

}
