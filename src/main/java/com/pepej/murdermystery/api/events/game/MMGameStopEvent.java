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

package com.pepej.murdermystery.api.events.game;

import com.pepej.murdermystery.api.events.MurderMysteryEvent;
import com.pepej.murdermystery.arena.Arena;
import org.bukkit.event.HandlerList;

/**
 * @author Plajer
 * @since 0.0.3b
 * <p>
 * Called when arena is stopped.
 */
public class MMGameStopEvent extends MurderMysteryEvent {

  private static final HandlerList HANDLERS = new HandlerList();

  public MMGameStopEvent(Arena arena) {
    super(arena);
  }

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

}
