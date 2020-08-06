package pl.plajer.murdermystery.handlers.gui;

import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.entity.Player;

/**
 * @author pepej
 * <p>
 * Created at 06.14.2020
 */
public interface GuiComponent {

    void injectComponents(StaticPane pane);

    void show(Player player);

}
