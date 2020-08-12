package pl.plajer.murdermystery.utils.message.type;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SubTitleMessage implements IMessage {

    @Getter
    private final String subTitle;

    @Getter
    private final int fadeIn;
    @Getter
    private final int stay;
    @Getter
    private final int fadeOut;

    /**
     * Creates new SubTitleMessage from parameters
     *
     * @param subTitle The title (lower text)
     * @param fadeIn   Fade in time in ticks
     * @param stay     Stay time in ticks
     * @param fadeOut  Fade out time in ticks
     */
    public SubTitleMessage(String subTitle, int fadeIn, int stay, int fadeOut) {
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    /**
     * Sends subtitle to player
     *
     * @param player The player
     */
    @Override
    public void send(Player player) {
        try {
            Method sendTitle = Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            sendTitle.invoke(player, null, subTitle, fadeIn, stay, fadeOut);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }


    }

}