package de.leetgeeks.jgl.physx;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointDef;

import java.util.ArrayList;
import java.util.List;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 04.07.2015
 * Time: 12:42
 */
public class PhysxSimulation {
    public interface CollisionListener {

        void onCollision(Fixture bodyA, Fixture bodyB);
    }

    private static Logger log = LogManager.getLogger(PhysxSimulation.class);

    private static float TIMESTEP = 1.0f/60.0f;
    private static int VELOCITY_ITERATIONS = 6;
    private static int POSITION_ITERATIONS = 2;

    private World world;
    private List<CollisionListener> collisionListeners = new ArrayList<>();


    private PhysxSimulation() {

    }

    public static PhysxSimulation createWithWorld(final Vec2 gravity) {
        final PhysxSimulation simulator = new PhysxSimulation();
        simulator.world = new World(gravity);
        simulator.world.setAllowSleep(true);
        simulator.world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                simulator.collisionListeners.forEach(collisionListener -> collisionListener.onCollision(contact.getFixtureA(), contact.getFixtureB()));
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

        return simulator;
    }

    public void addCollisionListener(final CollisionListener listener) {
        this.collisionListeners.add(listener);
    }

    public Joint createJoint(final JointDef def) {
        return world.createJoint(def);
    }

    public <T> void destroyBody(PhysxBody<T> body) {
        world.destroyBody(body.getBody());
    }

    public <T> PhysxBody<T> createRectangle(float width, float height, Vec2 position, T payload, boolean dynamic) {
        PhysxBody<T> body = createRectangle(width, height, position, payload, dynamic, 0.3f, 0, 0.3f);
        return body;
    }


    public <T> PhysxBody<T> createKinematicRectangle(float width, float height, Vec2 position, T payload, float density, float restitution, float friction) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.KINEMATIC;
        bodyDef.position.set(position);
        bodyDef.fixedRotation = false;
        bodyDef.angularDamping = 0.2f;

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        Body body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.restitution = restitution;
        fixtureDef.friction = friction;
        final Fixture fixture = body.createFixture(fixtureDef);

        // Create wrapper body
        PhysxBody<T> result = new PhysxBody<>();
        result.setBody(body);
        result.setPayload(payload);

        return result;

    }

    public <T> PhysxBody<T> createRectangle(float width, float height, Vec2 position, T payload, boolean dynamic, float density, float restitution, float friction) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = dynamic ? BodyType.DYNAMIC : BodyType.STATIC;
        bodyDef.position.set(position);
        bodyDef.fixedRotation = false;
        bodyDef.angularDamping = 0.2f;

        final PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        Body body = world.createBody(bodyDef);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.restitution = restitution;
        fixtureDef.friction = friction;
        final Fixture fixture = body.createFixture(fixtureDef);

        // Create wrapper body
        PhysxBody<T> result = new PhysxBody<>();
        result.setBody(body);
        result.setPayload(payload);

        return result;
    }

    /*public <T> PhysxBody<T> createCircle(float radius, Vec2 position, T payload) {
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
    }*/

    public void simulate() {
        world.step(TIMESTEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
    }
}
