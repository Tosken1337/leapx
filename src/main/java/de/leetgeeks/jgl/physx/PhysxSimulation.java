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
import java.util.Optional;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 04.07.2015
 * Time: 12:42
 */
public class PhysxSimulation {
    public interface CollisionListener {

        void onCollision(PhysxBody<?> bodyA, PhysxBody<?> bodyB);
    }

    private static Logger log = LogManager.getLogger(PhysxSimulation.class);

    private static float TIMESTEP = 1.0f/60.0f;
    private static int VELOCITY_ITERATIONS = 6;
    private static int POSITION_ITERATIONS = 2;

    private World world;

    private CollisionListener collisionListener;

    /**
     * Contains all bodies
     */
    private List<PhysxBody<?>> bodies = new ArrayList<>();

    private PhysxSimulation() {

    }

    public static PhysxSimulation createWithWorld(final Vec2 gravity) {
        final PhysxSimulation simulator = new PhysxSimulation();
        simulator.world = new World(gravity);
        simulator.world.setAllowSleep(true);
        simulator.world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                if (simulator.collisionListener != null) {
                    Optional<PhysxBody<?>> bodyA = simulator.getBodyForFixture(contact.getFixtureA());
                    Optional<PhysxBody<?>> bodyB = simulator.getBodyForFixture(contact.getFixtureB());

                    simulator.collisionListener.onCollision(bodyA.get(), bodyB.get());
                }
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

    public void setCollisionListener(final CollisionListener listener) {
        this.collisionListener = listener;
    }

    public Joint createJoint(final JointDef def) {
        return world.createJoint(def);
    }

    public <T> PhysxBody<T> createRectangle(float width, float height, Vec2 position, T payload, boolean dynamic) {
        PhysxBody<T> body = createRectangle(width, height, position, payload, dynamic, 0.3f, 0);
        bodies.add(body);
        return body;
    }


    private <T> PhysxBody<T> createRectangle(float width, float height, Vec2 position, T payload, boolean dynamic, float density, float restitution) {
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

    private Optional<PhysxBody<?>> getBodyForFixture(final Fixture fixture) {
        return bodies.stream()
                .filter(physxBody -> physxBody.getBody().getFixtureList().equals(fixture))
                .findFirst();
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
