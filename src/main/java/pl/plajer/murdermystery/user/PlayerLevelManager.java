package pl.plajer.murdermystery.user;

import pl.plajer.murdermystery.arena.ArenaRegistry;

public class PlayerLevelManager  {

    private int level;
    private int xp;

    public PlayerLevelManager(int level, int xp) {
        this.level = level;
        this.xp = xp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }
}
