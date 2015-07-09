package de.leetgeeks.jgl.physx;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.MassData;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 04.07.2015
 * Time: 12:42
 */
public class PhysxSimulation {
    private static float TIMESTEP = 1.0f/60.0f;
    private static int VELOCITY_ITERATIONS = 6;
    private static int POSITION_ITERATIONS = 2;

    private World world;

    private PhysxSimulation() {
    }

    public static PhysxSimulation createWithWorld(final Vec2 gravity) {
        final PhysxSimulation simulator = new PhysxSimulation();
        simulator.world = new World(gravity);
        simulator.world.setAllowSleep(true);
        return simulator;
    }

    public <T> PhysxBody<T> createRectangle(float width, float height, Vec2 position, T payload, boolean dynamic) {
        return createRectangle(width, height, position, payload, dynamic, 0.3f, 0);
    }


    public <T> PhysxBody<T> createRectangle(float width, float height, Vec2 position, T payload, boolean dynamic, float density, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = dynamic ? BodyType.DYNAMIC : BodyType.STATIC;
        bodyDef.position.set(position);

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        Body body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.restitution = restitution;
        fixtureDef.friction = 0.3f;
        final Fixture fixture = body.createFixture(fixtureDef);

        // Create wrapper body
        PhysxBody<T> result = new PhysxBody<>();
        result.setBody(body);
        result.setPayload(payload);

        return result;
    }

    public <T> PhysxBody<T> createCircle(float radius, Vec2 position, T payload) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(position);

        Body body = world.createBody(bodyDef);
        final CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.density = 1;
        fixtureDef.friction = 0.3f;
        final Fixture fixture = body.createFixture(fixtureDef);
        final MassData m = new MassData();
        m.I = 1;
        body.setMassData(m);

        // Create wrapper body
        PhysxBody<T> result = new PhysxBody<>();
        result.setBody(body);
        result.setPayload(payload);

        return result;
    }

    public void simulate() {
        world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }
}
