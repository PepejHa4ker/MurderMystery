package com.pepej.murdermystery.arena.special.trap;

import lombok.Data;
import org.bukkit.Location;

@Data
public class Trap {

    Location trapLocation;
    Location to;
    int cooldown;
}
