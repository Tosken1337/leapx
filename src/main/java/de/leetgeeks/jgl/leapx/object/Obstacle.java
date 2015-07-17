package de.leetgeeks.jgl.leapx.object;

import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 13:38
 */
public class Obstacle extends GameObject {

    public Obstacle(Vector2f centerPosition, Vector2f dimension, float angle) {
        super(centerPosition, dimension, angle);
    }
}
