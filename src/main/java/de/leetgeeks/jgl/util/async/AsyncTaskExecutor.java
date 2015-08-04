package de.leetgeeks.jgl.util.async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 02.08.2015
 * Time: 12:13
 */
public final class AsyncTaskExecutor {
    private static AsyncTaskExecutor instance;

    private final ExecutorService pool;

    private AsyncTaskExecutor() {
        pool = Executors.newFixedThreadPool(4);
    }

    public static AsyncTaskExecutor getInstance() {
        if (instance == null) {
            instance = new AsyncTaskExecutor();
        }
        return instance;
    }

    public CompletableFuture<String> runAsync() {
        final CompletableFuture<String> test = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "asd";
        }, pool);

        return test;
    }
}
