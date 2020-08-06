package pl.plajer.murdermystery.arena.special.trap;

import org.bukkit.Location;

public class Trap {

    private Location trapLocation;
    private Location to;
    private int cooldown;

    public Trap(Location trapLocation, Location to, int cooldown) {
        this.trapLocation = trapLocation;
        this.to = to;
        this.cooldown = cooldown;
    }

    public Location getTo() {
        return to;
    }

    public Location getTrapLocation() {
        return trapLocation;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }
}
