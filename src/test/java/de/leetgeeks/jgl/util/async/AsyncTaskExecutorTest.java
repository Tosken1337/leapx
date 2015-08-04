package de.leetgeeks.jgl.util.async;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 02.08.2015
 * Time: 16:50
 */
public class AsyncTaskExecutorTest {

    @Test
    public void testRunAsync() throws Exception {
        final CompletableFuture<String> test = AsyncTaskExecutor.getInstance().runAsync();
        test.thenAccept(s -> System.out.printf(s));
        System.out.println("Created");

        TimeUnit.SECONDS.sleep(10);
    }
}