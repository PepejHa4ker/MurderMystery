package com.pepej.murdermystery.utils.effects.particle;

import com.pepej.murdermystery.utils.misc.Reflection;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

public class ParticlePlayer {

    private static Constructor<?> packetConstructor;

    private static Method valueOf;

    static { // Get classes

        //region NMS
        Class<?> packetPlayOutWorldParticlesClass = Reflection.getNMSClass("PacketPlayOutWorldParticles");

        Class<?> enumParticleClass = Reflection.getNMSClass("EnumParticle");

        try {
            valueOf = enumParticleClass.getMethod("valueOf", String.class);
            packetConstructor = packetPlayOutWorldParticlesClass.getConstructor(
                    enumParticleClass,
                    boolean.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    float.class,
                    int.class,
                    int[].class
            );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    //endregion

    private Object particle;

    /**
     * Creates new instance of ParticlePlayer
     *
     * @param particle Type of particle to be displayed
     */
    public ParticlePlayer(Particle particle) {
        try {
            this.particle = valueOf.invoke(null, particle.name());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates new instance of ParticlePlayer
     *
     * @param particleName Name of particle to be displayed
     * @deprecated Use {@link #ParticlePlayer(Particle)} instead
     */
    @Deprecated
    public ParticlePlayer(String particleName) {
        this(Particle.valueOf(particleName));
    }

    /**
     * Plays effect to all players in list
     *
     * @param location Location to play the particles in
     * @param amount   Amount of particles
     * @param players  Players to play the particles to
     */
    public void play(Location location, int amount, Player... players) {
        Object packet = createPacket(
                particle,
                (float) location.getX(),
                (float) location.getY(),
                (float) location.getZ(),
                0f,
                0f,
                0f,
                0f, amount
        );

        for (Player player : players) {
            Reflection.sendPacket(player, packet);
        }
    }

    /**
     * Plays effect to all players in world
     *
     * @param location Location to play the particles in
     * @param amount   Amount of particles
     */
    public void play(Location location, int amount) {
        List<Player> players = location.getWorld().getPlayers();
        play(location, amount, players.toArray(new Player[players.size()]));
    }

    /**
     * Creates packet from arguments
     *
     * @param particle Particle to play
     * @param x        X location
     * @param y        Y location
     * @param z        Z location
     * @param xOffset  X offset
     * @param yOffset  Y offset
     * @param zOffset  Z offset
     * @param data     Data
     * @param amount   Amount of particles
     * @return The packet
     */
    private Object createPacket(
            Object particle,
            float x,
            float y,
            float z,
            float xOffset,
            float yOffset,
            float zOffset,
            float data,
            int amount
    ) {
        try {

            return packetConstructor.newInstance(particle, true, x, y, z, xOffset, yOffset, zOffset, data, amount, null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
