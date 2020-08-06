package pl.plajer.murdermystery.utils.message;

import lombok.NonNull;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.utils.misc.Reflection;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSClasses {

    //region NMS
    public static Class<?> chatSerializerClass;
    public static Class<?> iChatBaseComponentClass;
    public static Class<?> packetPlayOutChatClass;
    public static Class<?> chatMessageTypeClass;

    public static Method a;
    public static Method valueOf;

    public static Constructor packetPlayOutTitleConstructor1;
    public static Constructor packetPlayOutTitleConstructor2;
    public static Constructor packetPlayOutChatConstructor;

    public static Object enumTitleActionTitle;
    public static Object enumTitleActionSubTitle;
    public static Object gameInfo;

    static {

        try {
            chatSerializerClass = Reflection.getNMSClass("IChatBaseComponent$ChatSerializer");
            a = chatSerializerClass.getMethod("a", String.class);
            iChatBaseComponentClass = Reflection.getNMSClass("IChatBaseComponent");
            packetPlayOutChatClass = Reflection.getNMSClass("PacketPlayOutChat");
            chatMessageTypeClass = Reflection.getNMSClass("ChatMessageType");
            gameInfo = chatMessageTypeClass.getMethod("valueOf", String.class).invoke(null, "GAME_INFO");
            packetPlayOutChatConstructor = packetPlayOutChatClass.getConstructor(iChatBaseComponentClass, chatMessageTypeClass);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
    //endregion

    /**
     * Sends title via NMS
     *
     * @param player   The player to send the title to
     * @param title    The title (can be null)
     * @param subTitle The sub title (can be null)
     * @param fadeIn   Fade in time in ticks
     * @param stay     Stay time in ticks
     * @param fadeOut  Fade out time in ticks
     */
    public static void sendTitlesNMS(Player player, @Nullable String title, @Nullable String subTitle, int fadeIn, int stay, int fadeOut) {
        try {

            if (title != null) {
                Object titleComponent = a.invoke(null, "{\"text\":\"" + title + "\"}");
                Object titlePacket = packetPlayOutTitleConstructor1.newInstance(enumTitleActionTitle, titleComponent);
                Reflection.sendPacket(player, titlePacket);
            }

            if (subTitle != null) {
                Object subTitleComponent = a.invoke(null, "{\"text\":\"" + subTitle + "\"}");
                Object subTitlePacket = packetPlayOutTitleConstructor1.newInstance(enumTitleActionSubTitle, subTitleComponent);
                Reflection.sendPacket(player, subTitlePacket);
            }

            if (title != null && subTitle != null) {
                Object timingsPacket = packetPlayOutTitleConstructor2.newInstance(fadeIn, stay, fadeOut);
                Reflection.sendPacket(player, timingsPacket);
            }

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends ActionBar over NMS
     *
     * @param player    The player to send the action bar to
     * @param actionBar Action bar text
     */
    public static void sendActionBarNMS(Player player, @NonNull String actionBar) {
        try {

            Object actionBarComponent = a.invoke(null, "{\"text\":\"" + actionBar + "\"}");
            Object packet;
            packet = packetPlayOutChatConstructor.newInstance(actionBarComponent, gameInfo);
            Reflection.sendPacket(player, packet);

        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

}
