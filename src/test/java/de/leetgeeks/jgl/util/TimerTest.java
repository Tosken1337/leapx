package de.leetgeeks.jgl.util;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 19.07.2015
 * Time: 12:16
 */
public class TimerTest {

    @Test
    public void testGetTimeMillis() throws Exception {
        Timer timer = new Timer();
        timer.start();

        System.out.println("Timer started");
        for (int i = 0; i < 10; i++) {
            TimeUnit.MILLISECONDS.sleep(250);
            double elapsedMillis = timer.getTimeMillis();
            System.out.println("Elapsed time " + elapsedMillis);
        }

        timer.pause();
        System.out.println("Timer paused at " + timer.getTimeMillis());

        for (int i = 0; i < 10; i++) {
            TimeUnit.MILLISECONDS.sleep(250);
            double elapsedMillis = timer.getTimeMillis();
            System.out.println("Elapsed time " + elapsedMillis);
        }

        System.out.println("Timer resumed");
        timer.resume();
        for (int i = 0; i < 10; i++) {
            TimeUnit.MILLISECONDS.sleep(250);
            double elapsedMillis = timer.getTimeMillis();
            System.out.println("Elapsed time " + elapsedMillis);
        }
    }
}