package de.leetgeeks.jgl.gl.camera;

import org.joml.Matrix4f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 05.07.2015
 * Time: 08:27
 */
public class OrthoCamera extends Camera {

    private float left;
    private float right;
    private float top;
    private float bottom;

    public OrthoCamera(float left, float right, float top, float bottom, float zNear, float zFar) {
        super(zNear, zFar);
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        viewProjectionMatrix = new Matrix4f()
                .setOrtho(left, right, bottom, top, zNear, zFar);
    }

    public float getLeft() {
        return left;
    }

    public float getRight() {
        return right;
    }

    public float getTop() {
        return top;
    }

    public float getBottom() {
        return bottom;
    }
}
