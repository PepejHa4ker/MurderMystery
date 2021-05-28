/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pepej.murdermystery.arena.special;

import lombok.AllArgsConstructor;
import lombok.Value;
import org.bukkit.Location;

/**
 * @author Plajer
 * <p>
 * Created at 15.10.2018
 */
@AllArgsConstructor
@Value
public class SpecialBlock {

  Location location;
  SpecialBlockType specialBlockType;
  
  public enum SpecialBlockType {
    HORSE_PURCHASE, MYSTERY_CAULDRON, PRAISE_DEVELOPER, RAPID_TELEPORTATION, MAGIC_ANVIL
  }

}
