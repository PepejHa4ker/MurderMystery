package pl.plajer.murdermystery.arena.special.trap;

import org.bukkit.event.Listener;
import pl.plajer.murdermystery.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrapRegistry implements Listener {

    private static Main plugin;
    private static List<Trap> traps = new ArrayList<>();
    private static Random rand;


    public static void init(Main plugin) {
        TrapRegistry.plugin = plugin;
        //good prayers
        rand = new Random();
    }

    public static List<Trap> getTraps() {
        return traps;
    }
}
