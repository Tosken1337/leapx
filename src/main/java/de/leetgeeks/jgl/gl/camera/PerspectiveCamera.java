package de.leetgeeks.jgl.gl.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 27.06.2015
 * Time: 21:06
 */
public class PerspectiveCamera extends Camera {
    private Matrix4f viewPerspective;
    private float fovy;
    private float aspect;
    private float zNear;
    private float zFar;

    /**
     *
     * @param eye
     *            the position of the camera
     * @param center
     *            the point in space to look at
     * @param up
     *            the direction of 'up'
     * @param fovy
     *            the vertical field of view in degrees
     * @param aspect
     *            the aspect ratio (i.e. width / height)
     * @param zNear
     *            near clipping plane distance
     * @param zFar
     *            far clipping plane distance
     */
    public PerspectiveCamera(Vector3f eye, Vector3f center, Vector3f up, float fovy, float aspect, float zNear, float zFar) {
        this.fovy = fovy;
        this.aspect = aspect;
        this.zNear = zNear;
        this.zFar = zFar;
        viewPerspective = new Matrix4f()
                .setPerspective(fovy, aspect, zNear, zFar)
                .lookAt(eye, center, up);
    }

    /**
     *
     * TODO Not working currently because it will ovveride the lookat
     * @param fovy
     *            the vertical field of view in degrees
     * @param aspect
     *            the aspect ratio (i.e. width / height)
     */
    public void setPerspective(float fovy, float aspect) {
        this.fovy = fovy;
        this.aspect = aspect;
        viewPerspective.setPerspective(fovy, aspect, zNear, zFar);
    }

    public void getViewProjection(final FloatBuffer buffer) {
        viewPerspective.get(buffer);
    }

    public Matrix4f getViewProjection() {
        return viewPerspective;
    }
}
