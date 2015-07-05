package de.leetgeeks.jgl.gl.camera;

import org.joml.Matrix4f;

import java.nio.FloatBuffer;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 27.06.2015
 * Time: 21:06
 */
public abstract class Camera {
    protected Matrix4f viewProjectionMatrix;
    protected float zNear;
    protected float zFar;

    protected Camera(float zNear, float zFar) {
        this.zNear = zNear;
        this.zFar = zFar;
    }

    public void getViewProjection(final FloatBuffer buffer) {
        viewProjectionMatrix.get(buffer);
    }

    public Matrix4f getViewProjection() {
        return viewProjectionMatrix;
    }
}
