package de.leetgeeks.jgl.util;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 19.07.2015
 * Time: 11:03
 */
public class Timer {
    private boolean isRunning = false;

    /**
     * The base nano totalElapsedTime to update the elapsed totalElapsedTime and update the totalElapsedTime when ticking the timer.
     */
    private long baseNanoTime = 0L;

    /**
     * The totalElapsedTime represents the real progress of the timer.
     */
    private long totalElapsedTime = 0L;

    /**
     * The sum of all elapsed time of any previous time spans (between resume and pause calls)
     */
    private long sumPreviousTimePeriods = 0L;


    public boolean start() {
        if (isRunning) {
            throw new IllegalStateException("Already running");
        }

        renewBaseTime();
        isRunning = true;

        return true;
    }

    public void pause() {
        isRunning = false;

        long currentTime = System.nanoTime();
        long timeSinceLastStartOrResume = currentTime - baseNanoTime;
        sumPreviousTimePeriods += timeSinceLastStartOrResume;
        // Time will not go on from now
        totalElapsedTime = sumPreviousTimePeriods;
    }

    public void resume() {
        renewBaseTime();
        isRunning = true;
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Returns the elapsed time since since start.
     * The timer will not elapse during pause state.
     *
     * @return  The elapsed time since first start.
     */
    public double getTimeMillis() {
        tick();
        return (totalElapsedTime) / 1E6;
    }

    public GameDuration getTime() {
        tick();
        return GameDuration.of(totalElapsedTime);
    }

    private void tick() {
        if (isRunning) {
            long currentTime = System.nanoTime();
            long sinceLastStartOrResume = currentTime - baseNanoTime;
            totalElapsedTime = sinceLastStartOrResume + sumPreviousTimePeriods;
        }
    }

    /**
     * Sets the current system nano totalElapsedTime as the reference / root totalElapsedTime.
     * This base totalElapsedTime will be used to update the elapsed totalElapsedTime when the timer gets a tick.
     */
    private void renewBaseTime() {
        baseNanoTime = System.nanoTime();
    }
}
