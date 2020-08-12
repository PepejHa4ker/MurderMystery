package pl.plajer.murdermystery.commands.arguments.data;

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


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;


/**
 * @author Plajer
 * <p>
 * Created at 11.01.2019
 */
@AllArgsConstructor
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class CommandArgument {

    @Getter
    private String argumentName;
    @Getter
    private List<String> permissions;
    @Getter
    private ExecutorType validExecutors;

    public CommandArgument(String argumentName, String permissions, ExecutorType validExecutors) {
        this.argumentName = argumentName;
        this.permissions = Collections.singletonList(permissions);
        this.validExecutors = validExecutors;
    }


    public void execute(CommandSender sender, String[] args) {
    }

    public enum ExecutorType {
        BOTH, CONSOLE, PLAYER
    }

}
