package pl.plajer.murdermystery;

import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.murdermystery.plugin.scheduler.AbstractScheduler;
import pl.plajer.murdermystery.plugin.scheduler.SchedulerAdapter;

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
