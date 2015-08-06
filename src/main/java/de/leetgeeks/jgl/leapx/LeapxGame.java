package de.leetgeeks.jgl.leapx;

import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.leapx.game.object.Player;
import de.leetgeeks.jgl.leapx.input.leap.LeapInput;
import de.leetgeeks.jgl.leapx.rendering.GameRenderer;
import de.leetgeeks.jgl.leapx.rendering.UIRenderer;
import de.leetgeeks.jgl.leapx.sound.AudioEngine;
import de.leetgeeks.jgl.math.MathHelper;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.physx.PhysxSimulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

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
    private GameRenderer gameRenderer;

    /**
     *
     */
    private UIRenderer uiRenderer;

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

    private AudioEngine audio;


    public LeapxGame() {
    }

    public void init(float arenaWidth, float arenaHeight) {
        log.debug("Initializing game using arena size of {} x {}", arenaWidth, arenaHeight);

        gameRenderer = new GameRenderer();

        uiRenderer = new UIRenderer();

        /*audio = new AudioEngine();
        try {
            audio.init();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // Initialize physx world with zero gravity
        physxSimulator = PhysxSimulation.createWithWorld(new Vec2(0, 0));
        physxSimulator.addCollisionListener(new PhysxSimulation.CollisionListener() {
            @Override
            public void onCollision(Fixture bodyA, Fixture bodyB) {
                //audio.explosion();
            }
        });

        obstacleBodys = new ArrayList<>();

        // Initialize leap input controller
        leap = new LeapInput();

        // Build arena
        initArena(arenaWidth, arenaHeight);

        // Init level (at the moment we will have obe level which can be loaded)
        initLevel();
    }

    public void initGlResources() {
        gameRenderer.init();
        uiRenderer.init();
    }

    public void windowResize(int width, int height) {
        final float projectionWidth = arena.getWidth();
        final float projectionHeight = projectionWidth * ((float)height / width);
        gameRenderer.windowResize(width, height, projectionWidth, projectionHeight);
        uiRenderer.windowResize(width, height, projectionWidth, projectionHeight);
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

        gameRenderer.onDraw(elapsedMillis, arena, gameLevel);
        uiRenderer.onDraw(elapsedMillis, arena, gameLevel);
    }

    private void processInput(double elapsedMillis) {
        leap.process();

        if (leap.pointableActive()) {
            if (!gameLevel.isRunning()) {
                gameLevel.resume();
            }

            final Vector2f pointableLocationDelta = leap.getPointableLocationDelta();
            if (!pointableLocationDelta.equals(MathHelper.ZERO_VECTOR)) {
                final Vec2 force = new Vec2(pointableLocationDelta.x * 15, pointableLocationDelta.y * 15);
                //final Vec2 force = new Vec2(pointableLocationDelta.x, pointableLocationDelta.y);
                gameLevel.applyForceOnPlayer(force);
            }
        } else if (gameLevel.isRunning()) {
            gameLevel.pause();
        }
    }

    public void onKey(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_F12 && action == 1) {

        }

        gameLevel.onKey(key, scancode, action, mods);
    }
}
