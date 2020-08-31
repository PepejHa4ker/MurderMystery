package pl.plajer.murdermystery.handlers.gui;

import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.entity.Player;


public interface GuiComponent {

    void injectComponents(StaticPane pane);

    void show(Player player);

}
