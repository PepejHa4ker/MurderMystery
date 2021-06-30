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

package com.pepej.murdermystery.arena.role;

import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import org.bukkit.entity.Player;

/**
 * @author Plajer
 * <p>
 * Created at 06.10.2018
 */
public enum Role {

  /**
   * Detective or fake detective role
   */
  ANY_DETECTIVE,

  /**
   * Detective role, he must kill murderer
   */
  DETECTIVE,
  /**
   * Detective role, innocent who picked up bow became fake detective because he wasn't
   * detective by default
   */
  FAKE_DETECTIVE,
  /**
   * Medic player role, must heal players and survive to win
   */
  MEDIC,
  /**
   * Innocent player role, must survive to win
   */
  INNOCENT,
  /**
   * Murderer role, must kill everyone to win
   */
  MURDERER;

  /**
   * Checks whether player is playing specified role or not
   *
   * @param role   role to check
   * @param player player to check
   * @return true if is playing it, false otherwise
   */
  public static boolean isRole(Role role, Player player) {
    Arena arena = ArenaRegistry.getArena(player);
    if (arena == null) {
      return false;
    }
    switch (role) {
      case DETECTIVE:
        if (!arena.isCharacterSet(Arena.CharacterType.DETECTIVE)) {
          return false;
        }
        return arena.getDetectiveList().contains(player);
      case FAKE_DETECTIVE:
        if (!arena.isCharacterSet(Arena.CharacterType.FAKE_DETECTIVE)) {
          return false;
        }
        return arena.getCharacter(Arena.CharacterType.FAKE_DETECTIVE).equals(player);
      case MURDERER:
        if (!arena.isCharacterSet(Arena.CharacterType.MURDERER)) {
          return false;
        }
        return arena.getMurdererList().contains(player);
      case MEDIC:
        if (!arena.isCharacterSet(Arena.CharacterType.MEDIC)) {
          return false;
        }
        return arena.getMedicList().contains(player);
      case ANY_DETECTIVE:
        return isRole(Role.DETECTIVE, player) || isRole(Role.FAKE_DETECTIVE, player);
      case INNOCENT:
        return !isRole(Role.MEDIC, player) && !isRole(Role.MURDERER, player) && !isRole(Role.ANY_DETECTIVE, player);
      default:
        return false;
    }
  }

}
