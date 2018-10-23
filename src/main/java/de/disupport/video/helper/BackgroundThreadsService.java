/*
 * Copyright (c) 2018 di support GmbH
 */

package de.disupport.video.helper;

import java.util.concurrent.*;

public enum BackgroundThreadsService {
    Instance;

    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
//    private final ExecutorService single = Executors.newSingleThreadExecutor();


    public void runBackgroundTask(Runnable command) {
        executor.submit(command);
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        return scheduler.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
}
