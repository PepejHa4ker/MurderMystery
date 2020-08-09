package pl.plajer.murdermystery.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import static java.lang.Math.*;

public final class Maths {


    public static Vector rotate(Vector v, Location location) {
        double yawR = location.getYaw() / 180.0 * PI;
        double pitchR = location.getYaw() / 180.0 * PI;
        v = rotateAroundAxisX(v, pitchR);
        v = rotateAroundAxisY(v, -yawR);
        return v;
    }

    public static Vector rotateAroundAxisX(Vector v, double angle) {
        angle = Math.toRadians(angle);
        double cos = cos(angle);
        double sin = sin(angle);
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        angle = -angle;
        angle = Math.toRadians(angle);
        double cos = cos(angle);
        double sin = sin(angle);
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }


    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        double x = cos(angle) * v.getX() - sin(angle) * v.getY();
        double y = sin(angle) * v.getX() - cos(angle) * v.getY();
        return v.setX(x).setY(y);
    }


}
