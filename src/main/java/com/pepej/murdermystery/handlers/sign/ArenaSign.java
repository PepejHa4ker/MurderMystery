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

package com.pepej.murdermystery.handlers.sign;

import com.pepej.murdermystery.arena.Arena;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import javax.annotation.Nullable;

public class ArenaSign {

  private final Sign sign;
  private Block behind;
  private final Arena arena;

  public ArenaSign(Sign sign, Arena arena) {
    this.sign = sign;
    this.arena = arena;
    setBehindBlock();
  }
  

  private void setBehindBlock() {
    this.behind = null;
    if (sign.getBlock().getType() == Material.WALL_SIGN) {
        this.behind = getBlockBehindLegacy();
    }
  }

  private Block getBlockBehindLegacy() {
    return sign.getBlock().getRelative(((org.bukkit.material.Sign) sign.getData()).getAttachedFace());
  }

  public Sign getSign() {
    return sign;
  }

  @Nullable
  public Block getBehind() {
    return behind;
  }

  public Arena getArena() {
    return arena;
  }

}
