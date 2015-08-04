package de.leetgeeks.jgl.leapx.game.level;

import de.leetgeeks.jgl.leapx.game.mechanic.EasyGameplayComputer;
import de.leetgeeks.jgl.leapx.game.mechanic.LevelStateComputer;
import de.leetgeeks.jgl.leapx.game.mechanic.ObstacleMagnetComputer;
import de.leetgeeks.jgl.leapx.game.mechanic.ShockwaveComputer;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.GameObject;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.leapx.game.object.Player;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.physx.PhysxSimulation;
import de.leetgeeks.jgl.util.GameDuration;
import de.leetgeeks.jgl.util.Timer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.joml.Vector2f;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 13.07.2015
 * Time: 19:23
 */
public class Level {
    private static final Logger log = LogManager.getLogger();

    private final PhysxSimulation physxSimulator;

    private final List<LevelStateComputer> levelStateComputer;

    private final Timer levelTimer;

    private List<PhysxBody<Obstacle>> obstacles;

    private PhysxBody<Player> player;

    private VisualHandicap visualHandicap;

    private LevelState state;

    private Random rand = new Random();

    public Level(PhysxSimulation physx) {
        obstacles = new ArrayList<>();
        levelTimer = new Timer();
        visualHandicap = VisualHandicap.None;
        physxSimulator = physx;
        state = LevelState.NotStarted;

        levelStateComputer = new ArrayList<>();
        levelStateComputer.add(new EasyGameplayComputer());
        levelStateComputer.add(new ShockwaveComputer());
        levelStateComputer.add(new ObstacleMagnetComputer());
    }

    public void init(final GameArena arena, float obstacleSize) {
        log.info("Initialize level");
        obstacles = spawnObstacles(arena, obstacleSize, 10);
        player = spawnPlayer(arena);
        levelStateComputer.forEach(computer -> computer.init(Level.this, arena));
        physxSimulator.addCollisionListener(new CollisionListener());

        // Perform one time step to resolve collisions
        simulatePhysx(0);
    }

    public void start() {
        if (levelTimer.isRunning()) {
            log.warn("Unable to start level. Level already started");
            return;
        }

        log.info("Level started");
        state = LevelState.Running;
        levelTimer.start();
    }

    public void pause() {
        log.debug("Pausing game");
        state = LevelState.Paused;
        levelTimer.pause();
    }

    public void resume() {
        log.debug("Resuming game");
        state = LevelState.Running;
        levelTimer.resume();
    }

    public boolean isRunning() {
        return levelTimer.isRunning();
    }

    public void update(double elapsedTime) {
        final GameDuration levelTime = levelTimer.getTime();
        if (state == LevelState.Running) {
            levelStateComputer.forEach(computer -> computer.update(levelTime));
            simulatePhysx(elapsedTime);
        }

        updateGameObjects(elapsedTime);
    }

    public void applyForceOnPlayer(final Vec2 force) {
        //@ TODO do not directly update the physx. let the rule engine decide if it should be applied or maybe scaled
        // to create different movement schemes or similar.
        player.getBody().applyLinearImpulse(force, player.getPosition());


        //player.getBody().setLinearVelocity(force.mul(100));
    }

    public List<Obstacle> getObstacles() {
        return obstacles.stream()
                .map(PhysxBody::getPayload)
                .collect(Collectors.toList());
    }

    public List<PhysxBody<Obstacle>> getObstaclePhysx() {
        return obstacles;
    }

    public Player getPlayer() {
        return player.getPayload();
    }

    public PhysxBody<Player> getPlayerPhysx() {
        return player;
    }

    public VisualHandicap getVisualHandicap() {
        return visualHandicap;
    }


    public void clearLevel() {

    }

    public double getTime() {
        return levelTimer.getTimeMillis();
    }

    public GameDuration getGameDuration() {
        return levelTimer.getTime();
    }

    public void switchVisualHandicap() {
        final List<VisualHandicap> valueList = Arrays.asList(VisualHandicap.values());
        final int currentIndex = valueList.indexOf(visualHandicap);
        activateVisualHandicap(valueList.get((currentIndex + 1) % valueList.size()));
    }

    public void deactivateVisualHandicap() {
        activateVisualHandicap(VisualHandicap.None);
    }

    public void activateRandomVisualHandycap() {
        final List<VisualHandicap> valueList = new ArrayList<>(Arrays.asList(VisualHandicap.values()));
        valueList.remove(VisualHandicap.None);
        activateVisualHandicap(valueList.get(rand.nextInt(valueList.size())));
    }

    public void activateVisualHandicap(VisualHandicap handicap) {
        log.info("Activating visual handicap {}", handicap);
        visualHandicap = handicap;
        visualHandicap.setActivationTime(getTime());
    }

    /**
     * Current player collides with obstacles.
     * @param obstacle  The obstacle which has collided with the player.
     */
    private void onPlayerObstacleCollision(final Obstacle obstacle) {
        log.debug("Player collides with obstacle {}", obstacle);
        final Optional<PhysxBody<Obstacle>> obstacleBody = getFromObstacle(obstacle);

        if (obstacleBody.isPresent()) {
            levelStateComputer.forEach(computer -> computer.playerCollision(obstacle));
        }
    }

    public void destroyObstacle(final Obstacle obstacle) {
        final Optional<PhysxBody<Obstacle>> obstacleBody = getFromObstacle(obstacle);

        if (obstacleBody.isPresent()) {
            physxSimulator.destroyBody(obstacleBody.get());
            obstacles.remove(obstacleBody.get());
        }
    }

    private Optional<PhysxBody<Obstacle>> getFromObstacle(final Obstacle obstacle) {
        return obstacles.stream()
                .filter(obstaclePhysxBody -> obstaclePhysxBody.getPayload().equals(obstacle))
                .findFirst();
    }

    private void updateGameObjects(double elapsedMillis) {
        Stream.concat(obstacles.stream(), Stream.of(player)).forEach(physxBody -> {
            Vec2 position = physxBody.getBody().getPosition();
            float angle = physxBody.getBody().getAngle();
            physxBody.getPayload().updateTransformation(new Vector2f(position.x, position.y), angle);
        });
    }

    private void simulatePhysx(double elapsedMillis) {
        physxSimulator.simulate();
    }


    /**
     *
     * @param arena
     * @param obstacleSize
     * @param numObstacles
     * @return
     */
    private List<PhysxBody<Obstacle>> spawnObstacles(final GameArena arena, float obstacleSize, int numObstacles) {
        final Random rand = new Random();
        final float validArenaWidth = arena.getWidth() - obstacleSize;
        final float validArenaHeight = arena.getHeight() - obstacleSize;
        final List<PhysxBody<Obstacle>> obstacles = new ArrayList<>();
        IntStream.range(0, numObstacles).forEach(value -> {
            // Compute random obstacle position within the valid arena. Position is in the center of the obstacle
            final float xPos = rand.nextFloat() * validArenaWidth + (obstacleSize / 2f);
            final float yPos = rand.nextFloat() * validArenaHeight + (obstacleSize / 2f);
            final Vector2f position = new Vector2f(xPos, yPos);
            final PhysxBody<Obstacle> obstacle = spawnObstacle(obstacleSize, position);
            obstacles.add(obstacle);
        });

        return obstacles;
    }

    /**
     *
     * @param size
     * @param position
     * @return
     */
    private PhysxBody<Obstacle> spawnObstacle(final float size, final Vector2f position) {
        final Obstacle obstacle = new Obstacle(position, new Vector2f(size, size), 0);

        // Create physx object for each obstacle
        final float density = 0.5f;
        final float restitution = 0.6f;
        final float friction = 0.2f;

        return physxSimulator.createRectangle(
                obstacle.getWidth(), obstacle.getHeight(),
                new Vec2(obstacle.getCenterPosition().x, obstacle.getCenterPosition().y),
                obstacle,
                true,
                density, restitution, friction);
    }

    /**
     *
     * @param arena
     * @return
     */
    private PhysxBody<Player> spawnPlayer(GameArena arena) {
        final Player player = new Player(new Vector2f(0, 0), new Vector2f(2.5f, 2.5f), 0);
        /*return physxSimulator.createKinematicRectangle(
                player.getWidth(),
                player.getHeight(),
                new Vec2(arena.getCenterPosition().x, arena.getCenterPosition().y),
                player,
                0.3f, 0f, 0.3f);*/


        return physxSimulator.createRectangle(
                player.getWidth(),
                player.getHeight(),
                new Vec2(arena.getCenterPosition().x, arena.getCenterPosition().y),
                player,
                true,
                0.3f, 0f, 0.3f);
    }

    private Optional<GameObject> getObjectFromFixture(final Fixture fixture) {
        Optional<PhysxBody<? extends GameObject>> matchingGameObject =
                Stream.concat(obstacles.stream(), Stream.of(player))
                        .filter(physxBody -> physxBody.getBody().getFixtureList().equals(fixture))
                        .findFirst();

        if (matchingGameObject.isPresent()) {
            return Optional.of(matchingGameObject.get().getPayload());
        } else {
            return Optional.empty();
        }
    }

    public void onKey(int key, int scancode, int action, int mods) {
        /*if (key == GLFW.GLFW_KEY_F1 && action == 1) {
            switchVisualHandicap();
        }*/

        levelStateComputer.forEach(computer -> computer.onKey(key, scancode, action, mods));
    }

    public void setState(LevelState state) {
        this.state = state;
    }

    public LevelState getState() {
        return state;
    }

    /**
     *
     */
    private class CollisionListener implements PhysxSimulation.CollisionListener {

        @Override
        public void onCollision(Fixture fixtureA, Fixture fixtureB) {
            final Optional<GameObject> objectA = getObjectFromFixture(fixtureA);
            final Optional<GameObject> objectB = getObjectFromFixture(fixtureB);
            if (objectA.isPresent() && objectB.isPresent() && (isPlayer(objectA.get()) || isPlayer(objectB.get()))) {
                GameObject obstacle = isObstacle(objectA.get()) ? objectA.get() : objectB.get();
                Level.this.onPlayerObstacleCollision((Obstacle) obstacle);
            }
        }

        private boolean isPlayer(final GameObject object) {
            return object instanceof Player;
        }

        private boolean isObstacle(final GameObject object) {
            return object instanceof Obstacle;
        }
    }
}
