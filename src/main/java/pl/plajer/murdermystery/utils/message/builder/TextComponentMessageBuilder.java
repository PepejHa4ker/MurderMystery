package pl.plajer.murdermystery.utils.message.builder;

import lombok.Getter;
import lombok.NonNull;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.handlers.ChatManager;

public class TextComponentMessageBuilder {

    @Getter
    private final Player player;
    @Getter
    private final String message;
    private HoverEvent hoverEvent;
    private ClickEvent clickEvent;
    private TextComponent textComponent;

    public TextComponentMessageBuilder(@NonNull final Player player, @NonNull final String message) {
        this.player = player;
        this.message = message;
    }

    public TextComponentMessageBuilder setHoverEvent(@NonNull HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        return this;
    }

    public TextComponentMessageBuilder setClickEvent(@NonNull ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }


    public TextComponentMessageBuilder create() {
        this.textComponent = new TextComponent(ChatManager.colorRawMessage(message));
        if (this.hoverEvent != null) {
            this.textComponent.setHoverEvent(hoverEvent);
        }
        if (this.clickEvent != null) {
            this.textComponent.setClickEvent(clickEvent);
        }

        return this;
    }

    public void send() {
        this.player.spigot().sendMessage(this.textComponent);
    }
}
