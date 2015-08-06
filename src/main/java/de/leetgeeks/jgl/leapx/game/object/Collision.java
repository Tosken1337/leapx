package de.leetgeeks.jgl.leapx.game.object;

import de.leetgeeks.jgl.util.GameDuration;
import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 06.08.2015
 * Time: 10:31
 */
public class Collision {
    public enum Type {
        PlayerObstacle,
        ObstacleObstacle
    }

    private Type type;

    private Vector2f position;

    private GameDuration timeOfImpact;

    public Collision(Type type, Vector2f position, GameDuration timeOfImpact) {
        this.type = type;
        this.position = position;
        this.timeOfImpact = timeOfImpact;
    }

    public Type getType() {
        return type;
    }

    public Vector2f getPosition() {
        return position;
    }

    public GameDuration getTimeOfImpact() {
        return timeOfImpact;
    }
}
