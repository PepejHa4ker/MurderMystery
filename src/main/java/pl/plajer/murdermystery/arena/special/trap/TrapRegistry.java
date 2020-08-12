package pl.plajer.murdermystery.arena.special.trap;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.event.Listener;
import pl.plajer.murdermystery.MurderMystery;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class TrapRegistry implements Listener {

    static MurderMystery plugin;
    static List<Trap> traps = new ArrayList<>();
    static Random rand;


    public static void init(MurderMystery plugin) {
        TrapRegistry.plugin = plugin;
        //good prayers
        rand = new Random();
    }

    public static List<Trap> getTraps() {
        return traps;
    }
}
