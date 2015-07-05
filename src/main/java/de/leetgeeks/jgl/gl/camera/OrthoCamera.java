package de.leetgeeks.jgl.gl.camera;

import org.joml.Matrix4f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 05.07.2015
 * Time: 08:27
 */
public class OrthoCamera extends Camera {

    public OrthoCamera(float left, float right, float top, float bottom, float zNear, float zFar) {
        super(zNear, zFar);
        viewProjectionMatrix = new Matrix4f()
                .setOrtho(left, right, bottom, top, zNear, zFar);
    }

}
