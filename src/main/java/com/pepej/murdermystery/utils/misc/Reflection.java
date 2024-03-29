package com.pepej.murdermystery.utils.misc;

import com.pepej.murdermystery.exception.InvalidArugmentException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class Reflection {

    /**
     * Gets net.minecraft.server class from name
     *
     * @param name Class name
     * @return The class
     */
    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        try {
            return Class.forName("net.minecraft.server." + version + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new InvalidArugmentException("Cannot find NMS Class");
        }
    }

    /**
     * Gets org.bukkit.craftbukkit class from name
     *
     * @param name Class name
     * @return The class
     */
    public static Class<?> getCraftBukkitClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new InvalidArugmentException("Cannot find NMS Class");
        }
    }

    /**
     * Sends packet to player connection
     *
     * @param player Player to send packet to
     * @param packet Packet to send
     */
    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (IllegalAccessException | NoSuchMethodException | NoSuchFieldException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a is the same as b or extends b
     *
     * @param a Class a
     * @param b Class b
     * @return True = class extends or is same
     */
    public static boolean extendsFrom(Class<?> a, Class<?> b) {
        return a == b || a.isAssignableFrom(b);
    }

}