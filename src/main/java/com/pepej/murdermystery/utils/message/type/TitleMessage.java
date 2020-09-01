package com.pepej.murdermystery.utils.message.type;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TitleMessage implements IMessage {

    @Getter
    private final String title;

    @Getter
    private final int fadeIn, stay, fadeOut;

    /**
     * Creates new TitleMessage from parameters
     *
     * @param title   The title (upper text)
     * @param fadeIn  Fade in time in ticks
     * @param stay    Stay time in ticks
     * @param fadeOut Fade out time in ticks
     */
    public TitleMessage(String title, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    /**
     * Sends title to player
     *
     * @param player The player
     */
    @Override
    public void send(Player player) {
        try {
            Method sendTitle = Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            sendTitle.invoke(player, title, null, fadeIn, stay, fadeOut);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
