package com.pepej.murdermystery.commands.arguments.data;

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
