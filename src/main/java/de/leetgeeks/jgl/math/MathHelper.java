package de.leetgeeks.jgl.math;

import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 15.07.2015
 * Time: 20:31
 */
public abstract class MathHelper {
    public static final Vector2f ZERO_VECTOR = new Vector2f(0, 0);

    public static double radToDeg(double radian) {
        return radian * 180.0 / Math.PI;
    }
}
