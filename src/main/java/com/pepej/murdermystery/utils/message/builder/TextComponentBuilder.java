package com.pepej.murdermystery.utils.message.builder;

import com.pepej.murdermystery.handlers.ChatManager;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Builder-pattern class for creating TextComponent objects, {@link TextComponent}
 *
 * @author pepej
 * @version 1.1
 * @since 1.5
 */


@Getter
public class TextComponentBuilder {

    private final String message;

    @Nullable
    private String hoverMessage, clickCommand;
    private TextComponent textComponent;

    public TextComponentBuilder(@NotNull final String message) {
        this.message = message;
    }

    /**
     * @param hoverMessage - message, than will be display when player hover the text
     */
    public TextComponentBuilder hoverMessage(@NotNull final String hoverMessage) {
        this.hoverMessage = hoverMessage;
        return this;
    }

    /**
     * @param clickCommand - command, than will be execute when player will click on message
     */
    public TextComponentBuilder clickCommand(@NotNull final String clickCommand) {
        this.clickCommand = clickCommand;
        return this;
    }


    /**
     * @return Created {@link TextComponent} object
     */

    public TextComponentBuilder create() {
        this.textComponent = new TextComponent(ChatManager.colorRawMessage(message));
        if (this.hoverMessage != null) {
            this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatManager.colorRawMessage(this.hoverMessage)).create()));
        }
        if (this.clickCommand != null) {
            this.textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand));
        }

        return this;
    }

    /**
     * @param player - the player to whom the message will be sent
     */

    public void send(@NotNull final Player player) {
        player.spigot().sendMessage(this.textComponent);
    }

}
