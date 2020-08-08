package pl.plajer.murdermystery.handlers.scheduler;

import lombok.Getter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class SchedulerThreadFactory implements ThreadFactory {

    @Getter
    private static AtomicInteger lastThreadID = new AtomicInteger(0);

    private String threadName;

    /**
     * Creates new Scheduler Thread Factory with given name
     *
     * @param threadName Thread name, use %THREAD_ID% as placeholder for Thread id
     */
    public SchedulerThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    /**
     * Creates new Thread
     *
     * @param runnable The Runnable
     * @return The Thread
     */
    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.setName(threadName.replace("%THREAD_ID%", Integer.toString(lastThreadID.incrementAndGet())));
        return thread;
    }

}