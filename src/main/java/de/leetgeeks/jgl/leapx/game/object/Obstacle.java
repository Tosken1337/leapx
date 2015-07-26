package de.leetgeeks.jgl.leapx.game.object;

import de.leetgeeks.jgl.util.GameDuration;
import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 13:38
 */
public class Obstacle extends GameObject {

    private boolean isEvading;
    private GameDuration evadeStartTime;

    public Obstacle(Vector2f centerPosition, Vector2f dimension, float angle) {
        super(centerPosition, dimension, angle);
    }

    public void startEvade(boolean isEvading, GameDuration timestamp) {
        this.isEvading = isEvading;
        this.evadeStartTime = timestamp;
    }

    public void stopEvade() {
        this.isEvading = false;
    }

    public boolean isEvading() {
        return isEvading;
    }

    @Override
    public String toString() {
        return "Obstacle at position " + this.centerPosition;
    }

    public GameDuration getEvadeStartTime() {
        return evadeStartTime;
    }
}
