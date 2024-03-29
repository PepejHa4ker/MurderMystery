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

package com.pepej.murdermystery.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author TomTheDeveloper
 * <p>
 * Contains all GameStates.
 */
@AllArgsConstructor
public enum ArenaState {

  WAITING_FOR_PLAYERS("Ожидание"), STARTING("Начало"), IN_GAME("Идёт-игра"), ENDING("Завершение"), RESTARTING("Перезапуск");

  @Getter String formattedName;

}
