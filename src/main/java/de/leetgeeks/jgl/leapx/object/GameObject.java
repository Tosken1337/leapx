package de.leetgeeks.jgl.leapx.object;

import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 13:36
 */
public abstract class GameObject {

    /**
     * The center position (center of mass)
     */
    protected Vector2f centerPosition;

    /**
     * The width and height of the object or it's aabb
     */
    protected Vector2f dimension;

    /**
     * Body rotation along the z-axis in radian
     */
    protected float angle;

    /**
     * Transformation matrix contains the necessary transformations for rendering
     */
    //protected Matrix4f transformationMatrix;

    public GameObject(Vector2f centerPosition, Vector2f dimension, float angle) {
        this.centerPosition = centerPosition;
        this.dimension = dimension;
        this.angle = angle;
    }

    public void updateTransformation(Vector2f centerPosition, float angle) {
        this.centerPosition.set(centerPosition);
        this.angle = angle;
    }

    public Vector2f getCenterPosition() {
        return centerPosition;
    }

    /*public Matrix4f getTransformationMatrix() {
        return transformationMatrix;
    }

    public void setTransformationMatrix(Matrix4f transformationMatrix) {
        this.transformationMatrix = transformationMatrix;
    }*/

    public float getAngle() {
        return angle;
    }

    public float getWidth() {
        return dimension.x;
    }

    public float getHeight() {
        return dimension.y;
    }

    public float getLeftBound() {
        return getCenterPosition().x - dimension.x / 2f;
    }

    public float getRightBound() {
        return getCenterPosition().x + dimension.x / 2f;
    }

    public float getTopBound() {
        return getCenterPosition().y + dimension.y / 2f;
    }

    public float getBottomBound() {
        return getCenterPosition().y - dimension.y / 2f;
    }
}
