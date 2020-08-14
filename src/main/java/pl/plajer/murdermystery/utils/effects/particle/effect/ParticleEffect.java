package pl.plajer.murdermystery.utils.effects.particle.effect;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("rawtypes")
public abstract class ParticleEffect {
    @NotNull protected Integer initialDelay, period;
    @NotNull protected TimeUnit unit;
    @NotNull protected ScheduledExecutorService scheduler;
    protected ScheduledFuture task;
    @NotNull protected Runnable runnable;

    /**
     * Used to create a new particle effect
     *
     * @param scheduler    Scheduler for Timing (See )
     * @param runnable     Code which gets executed on particle update
     * @param initialDelay Start animation after x millis
     * @param period       Run code every x millis
     * @param unit         Unit of time (Milliseconds recommended)
     */
    protected ParticleEffect(
            @NotNull ScheduledExecutorService scheduler,
            @NotNull Runnable runnable,
            @NotNull Integer initialDelay,
            @NotNull Integer period,
            @NotNull TimeUnit unit
    ) {
        this.scheduler = scheduler;
        this.runnable = runnable;
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
    }

    /**
     * Play particle effect
     *
     * @return The effect
     */
    public ParticleEffect play() {
        task = scheduler.scheduleAtFixedRate(runnable, initialDelay, period, unit);
        return this;
    }

    /**
     * Stop particle effect
     *
     * @return The effect
     */
    public ParticleEffect stop() {
        if (task != null && !task.isCancelled())
            task.cancel(true);

        task = null;
        return this;
    }

}