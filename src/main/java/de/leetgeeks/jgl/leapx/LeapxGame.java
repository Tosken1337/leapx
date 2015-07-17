package de.leetgeeks.jgl.leapx;

import de.leetgeeks.jgl.leapx.input.leap.LeapInput;
import de.leetgeeks.jgl.leapx.object.GameArena;
import de.leetgeeks.jgl.leapx.object.Level;
import de.leetgeeks.jgl.leapx.object.Obstacle;
import de.leetgeeks.jgl.leapx.object.Player;
import de.leetgeeks.jgl.leapx.rendering.Renderer;
import de.leetgeeks.jgl.math.MathHelper;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.physx.PhysxSimulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
        physxSimulator.setCollisionListener(new CollisionListener());

        obstacleBodys = new ArrayList<>();

        // Initialize leap input controller
        leap = new LeapInput();

        // Build arena
        initArena(arenaWidth, arenaHeight);

        // Init level (at the moment we will have obe level which can be loaded)
        initLevel();

        initPlayer();
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
        gameLevel = new Level();

        // Spawn obstacles at random position within the game arena
        final List<Obstacle> obstacles = gameLevel.spawnObstacles(arena, 2);

        // Create physx object for each obstacle
        final float density = 0.3f;
        final float restitution = 0f;
        final float friction = 0.3f;
        obstacles.forEach(obstacle -> {
            final Vector2f centerPosPhysx = obstacle.getCenterPosition();
            final PhysxBody<Obstacle> obstacleBody = physxSimulator.createRectangle(
                    obstacle.getWidth(), obstacle.getHeight(),
                    new Vec2(centerPosPhysx.x, centerPosPhysx.y),
                    obstacle,
                    true,
                    density, restitution, friction);

            obstacleBodys.add(obstacleBody);
        });
    }

    /**
     *
     */
    private void initPlayer() {
        final Player player = new Player(new Vector2f(0, 0), new Vector2f(4, 4), 0);
        this.player = physxSimulator.createRectangle(
                player.getWidth(),
                player.getHeight(),
                new Vec2(arena.getCenterPosition().x , arena.getCenterPosition().y),
                player,
                true,
                0.3f, 0f, 0.3f);
    }

    /**
     * Clear arena obstacles, ...
     */
    private void clearLevel() {
        gameLevel.clearLevel();
        //@TODO destroy bodies
        obstacleBodys.clear();
    }


    public void loop(double elapsedMillis) {
        processInput(elapsedMillis);
        simulatePhysx(elapsedMillis);
        updateGameObjects(elapsedMillis);
        drawGameObjects(elapsedMillis);
    }

    private void drawGameObjects(double elapsedMillis) {
        renderer.onDraw(elapsedMillis, arena, gameLevel.getObstacles(), player.getPayload());
    }

    private void updateGameObjects(double elapsedMillis) {
        Stream.concat(obstacleBodys.stream(), Stream.of(player)).forEach(physxBody -> {
            Vec2 position = physxBody.getBody().getPosition();
            float angle = physxBody.getBody().getAngle();
            physxBody.getPayload().updateTransformation(new Vector2f(position.x, position.y), angle);
        });
    }

    private void simulatePhysx(double elapsedMillis) {
        physxSimulator.simulate();
    }

    private void processInput(double elapsedMillis) {
        leap.process();

        Vector2f pointableLocationDelta = leap.getPointableLocationDelta();
        if (!pointableLocationDelta.equals(MathHelper.ZERO_VECTOR)) {
            player.getBody().applyLinearImpulse((new Vec2(pointableLocationDelta.x * 8, pointableLocationDelta.y * 8)), player.getPosition());
        }
    }

    /**
     *
     */
    private static class CollisionListener implements PhysxSimulation.CollisionListener {

        @Override
        public void onCollision(Fixture bodyA, Fixture bodyB) {

        }
    }
}
