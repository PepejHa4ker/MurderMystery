package com.pepej.murdermystery.handlers.gui.setup.components;

import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.pepej.murdermystery.handlers.gui.setup.SetupInventory;
import org.bukkit.entity.Player;

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
