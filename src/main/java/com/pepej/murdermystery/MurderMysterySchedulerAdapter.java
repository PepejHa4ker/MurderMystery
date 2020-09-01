package com.pepej.murdermystery;

import com.pepej.murdermystery.plugin.scheduler.AbstractScheduler;
import com.pepej.murdermystery.plugin.scheduler.SchedulerAdapter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executor;

public class MurderMysterySchedulerAdapter extends AbstractScheduler implements SchedulerAdapter {
    private final Executor sync;

    public MurderMysterySchedulerAdapter(JavaPlugin plugin) {
        this.sync = r -> plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}
