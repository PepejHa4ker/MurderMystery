package pl.plajer.murdermystery.utils.number;

import org.bukkit.Location;
import org.bukkit.util.Vector;


public final class Maths {


    public static Vector rotate(Vector v, Location location) {
        double yawRadian = Math.toRadians(location.getYaw());
        double pitchRadian = Math.toRadians(location.getYaw());
        v = rotateAroundAxisX(v, yawRadian);
        v = rotateAroundAxisY(v, -pitchRadian);
        return v;
    }

    public static Vector rotateAroundAxisX(Vector v, double angle) {
        double y = v.getY() * Maths.cosRad(angle) - v.getZ() * Maths.sinRad(angle);
        double z = v.getY() * Maths.sinRad(angle) + v.getZ() * Maths.cosRad(angle);
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        angle = -angle;
        double x = v.getX() * Maths.cosRad(angle) + v.getZ() * Maths.sinRad(angle);
        double z = v.getX() * -Maths.sinRad(angle) + v.getZ() * Maths.cosRad(angle);
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        double x = v.getX() * Maths.cosRad(angle) - v.getY() * Maths.sinRad(angle);
        double y = v.getX() * Maths.sinRad(angle) - v.getY() * Maths.cosRad(angle);
        return v.setX(x).setY(y);
    }

    public static double cosRad(double rad) {
        rad = Math.toRadians(rad);
        return Math.cos(rad);

    }

    public static double sinRad(double rad) {
        rad = Math.toRadians(rad);
        return Math.sin(rad);
    }


}
