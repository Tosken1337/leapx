package de.leetgeeks.jgl.physx;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 04.07.2015
 * Time: 13:13
 */
public class PhysxBody<T> {
    private T payload;

    private Body body;

    public T getPayload() {
        return payload;
    }

    void setPayload(T payload) {
        this.payload = payload;
    }

    public Vec2 getPosition() {
        return body.getPosition();
    }

    public Body getBody() {
        return body;
    }

    void setBody(Body body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "PhysxBody{" +
                "position =" + body.getPosition() +
                '}';
    }
}
