package com.pepej.murdermystery.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.experimental.FieldDefaults;

@Value
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Rank {
    String name;
    int xp;
}


