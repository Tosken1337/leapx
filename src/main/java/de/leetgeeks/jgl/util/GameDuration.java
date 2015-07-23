package de.leetgeeks.jgl.util;

import java.time.Duration;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 23.07.2015
 * Time: 20:54
 */
public final class GameDuration {
    private Duration sinceStart;

    public static GameDuration of(long nanosSinceStart) {
        return new GameDuration(nanosSinceStart);
    }

    private GameDuration(long nanosSinceStart) {
        this.sinceStart = Duration.ofNanos(nanosSinceStart);
    }

    private GameDuration(Duration duration) {
        this.sinceStart = duration;
    }

    public GameDuration plus(long seconds) {
        return new GameDuration(sinceStart.plusSeconds(seconds));
    }

    public GameDuration minus(long seconds) {
        return new GameDuration(sinceStart.minusSeconds(seconds));
    }

    public GameDuration plus(GameDuration duration) {
        return new GameDuration(sinceStart.plus(duration.sinceStart));
    }

    public GameDuration minus(GameDuration duration) {
        return new GameDuration(sinceStart.minus(duration.sinceStart));
    }

    public boolean isOlderThan(GameDuration duration) {
        return sinceStart.compareTo(duration.sinceStart) == -1;
    }

    @Override
    public String toString() {
        long minutes = sinceStart.toMinutes();
        long seconds = sinceStart.getSeconds() - (minutes * 60);
        long millis = sinceStart.toMillis() - (seconds * 1000);
        return String.format("%02d:%02d:%03d", minutes, seconds, millis);
    }
}
