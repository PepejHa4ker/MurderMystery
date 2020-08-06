package pl.plajer.murdermystery.handlers.gui.setup.components;

import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.handlers.gui.setup.SetupInventory;

public class TrapRegisterComponent implements ArenaSetupGuiComponent {

    private SetupInventory setupInventory;

    @Override
    public void prepare(SetupInventory setupInventory) {
        this.setupInventory = setupInventory;
    }


    @Override
    public void injectComponents(StaticPane pane) {

    }

    @Override
    public void show(Player player) {

    }
}
