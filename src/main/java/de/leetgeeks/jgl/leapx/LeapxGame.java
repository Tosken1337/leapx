package de.leetgeeks.jgl.leapx;

import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.leapx.game.object.Player;
import de.leetgeeks.jgl.leapx.input.leap.LeapInput;
import de.leetgeeks.jgl.leapx.rendering.Renderer;
import de.leetgeeks.jgl.math.MathHelper;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.physx.PhysxSimulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 13:35
 */
public class LeapxGame {
    private static final Logger log = LogManager.getLogger();

    /**
     *
     */
    private PhysxSimulation physxSimulator;

    /**
     *
     */
    private Renderer renderer;

    /**
     *
     */
    private LeapInput leap;

    /**
     *
     */
    private GameArena arena;

    /**
     *
     */
    private Level gameLevel;

    /**
     *
     */
    private PhysxBody<Player> player;

    /**
     *
     */
    private List<PhysxBody<Obstacle>> obstacleBodys;


    public LeapxGame() {
    }

    public void init(float arenaWidth, float arenaHeight) {
        log.debug("Initializing game using arena size of {} x {}", arenaWidth, arenaHeight);

        renderer = new Renderer();

        // Initialize physx world with zero gravity
        physxSimulator = PhysxSimulation.createWithWorld(new Vec2(0, 0));

        obstacleBodys = new ArrayList<>();

        // Initialize leap input controller
        leap = new LeapInput();

        // Build arena
        initArena(arenaWidth, arenaHeight);

        // Init level (at the moment we will have obe level which can be loaded)
        initLevel();
    }

    public void initGlResources() {
        renderer.init();
    }

    public void windowResize(int width, int height) {
        final float projectionWidth = arena.getWidth();
        final float projectionHeight = projectionWidth * ((float)height / width);
        renderer.windowResize(width, height, projectionWidth, projectionHeight);
    }

    private void initArena(float arenaWidth, float arenaHeight) {
        arena = new GameArena(arenaWidth, arenaHeight);

        // Build arena bounds
        float leftBound = arena.getLeftBound();
        float rightBound = arena.getRightBound();
        float topBound = arena.getTopBound();
        float bottomBound = arena.getBottomBound();
        final Vector2f arenaCenter = arena.getCenterPosition();
        final int boundThickness = 1;
        PhysxBody<GameArena> leftBoundBody =    physxSimulator.createRectangle(boundThickness, arenaHeight, new Vec2(leftBound - boundThickness / 2f, arenaCenter.y), arena, false);
        PhysxBody<GameArena> rightBoundBody =   physxSimulator.createRectangle(boundThickness, arenaHeight, new Vec2(rightBound + boundThickness / 2f, arenaCenter.y), arena, false);
        PhysxBody<GameArena> topBoundBody =     physxSimulator.createRectangle(arenaWidth, boundThickness, new Vec2(arenaCenter.x, topBound + boundThickness / 2f), arena, false);
        PhysxBody<GameArena> bottomBoundBody =  physxSimulator.createRectangle(arenaWidth, boundThickness, new Vec2(arenaCenter.x, bottomBound - boundThickness / 2f), arena, false);
    }

    /**
     * Spawn new obstacles
     */
    private void initLevel() {
        //@ TODO load level settings (num obstacles, difficulty setinngs, ...) from xml file
        gameLevel = new Level(physxSimulator);
        gameLevel.init(arena, 1.5f);

        //@TODO remove dependency for physxbodys here. We should not use them
        // The level should be the only one handling the physx
        this.obstacleBodys = gameLevel.getObstaclePhysx();
        this.player = gameLevel.getPlayerPhysx();
    }

    /**
     * Clear arena obstacles, ...
     */
    private void clearLevel() {
        gameLevel.clearLevel();
        obstacleBodys.clear();
    }


    public void loop(double elapsedMillis) {
        processInput(elapsedMillis);

        gameLevel.update(elapsedMillis);

        drawGameObjects(elapsedMillis);
    }

    private void drawGameObjects(double elapsedMillis) {
        renderer.onDraw(elapsedMillis, arena, gameLevel);
    }

    private void processInput(double elapsedMillis) {
        leap.process();

        if (leap.pointableActive()) {
            if (!gameLevel.isRunning()) {
                gameLevel.resume();
            }

            final Vector2f pointableLocationDelta = leap.getPointableLocationDelta();
            if (!pointableLocationDelta.equals(MathHelper.ZERO_VECTOR)) {
                final Vec2 force = new Vec2(pointableLocationDelta.x * 8, pointableLocationDelta.y * 8);
                gameLevel.applyForceOnPlayer(force);
            }
        } else if (gameLevel.isRunning()) {
            gameLevel.pause();
        }
    }

/*    private Optional<Player> getPlayerFromFixture(final Fixture fixture) {
        if (player.getBody().getFixtureList().equals(fixture)) {
            return Optional.of(player.getPayload());
        } else {
            return Optional.empty();
        }
    }

    private Optional<Obstacle> getObstacleFromFixture(final Fixture fixture) {
        for (PhysxBody<Obstacle> obstacleBody : obstacleBodys) {
            if (obstacleBody.getBody().getFixtureList().equals(fixture)) {
                return Optional.ofNullable(obstacleBody.getPayload());
            }
        }
        return Optional.empty();
    }

    private Optional<GameObject> getObjectFromFixture(final Fixture fixture) {
        Optional<PhysxBody<? extends GameObject>> matchingGameObject =
                Stream.concat(obstacleBodys.stream(), Stream.of(player))
                        .filter(physxBody -> physxBody.getBody().getFixtureList().equals(fixture))
                        .findFirst();

        if (matchingGameObject.isPresent()) {
            return Optional.of(matchingGameObject.get().getPayload());
        } else {
            return Optional.empty();
        }
    }

    *//**
     *
     *//*
    private class CollisionListener implements PhysxSimulation.CollisionListener {

        @Override
        public void onCollision(Fixture fixtureA, Fixture fixtureB) {
            final Optional<GameObject> objectA = getObjectFromFixture(fixtureA);
            final Optional<GameObject> objectB = getObjectFromFixture(fixtureB);
            if (objectA.isPresent() && objectB.isPresent() && (isPlayer(objectA.get()) || isPlayer(objectB.get()))) {
                GameObject obstacle = isObstacle(objectA.get()) ? objectA.get() : objectB.get();
                gameLevel.onPlayerObstacleCollision((Obstacle) obstacle);
            }
        }

        private boolean isPlayer(final GameObject object) {
            return object instanceof Player;
        }

        private boolean isObstacle(final GameObject object) {
            return object instanceof Obstacle;
        }


    }*/
}
