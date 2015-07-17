package de.leetgeeks.jgl.leapx.object;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 13.07.2015
 * Time: 19:23
 */
public class Level {
    private static final Logger log = LogManager.getLogger();

    //@TODO read from level description xml
    private int numObstacles = 10;

    private List<Obstacle> obstacles;

    public Level() {
        obstacles = new ArrayList<>();
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Obstacle> spawnObstacles(final GameArena arena, float obstacleSize) {
        final Random rand = new Random();

        final float validArenaWidth = arena.getWidth() - obstacleSize;
        final float validArenaHeight = arena.getHeight() - obstacleSize;
        IntStream.range(0, numObstacles).forEach(value -> {
            // Compute random obstacle position within the valid arena
            // Computed position is in the center of the obstacle
            final float xPos = rand.nextFloat() * validArenaWidth + (obstacleSize / 2f);
            final float yPos = rand.nextFloat() * validArenaHeight + (obstacleSize / 2f);

            final Vector2f position = new Vector2f(xPos, yPos);
            final DecimalFormat format = new DecimalFormat();
            log.debug("Spawning obstacle at position {}", position.toString(format));
            final Obstacle obstacle = new Obstacle(position, new Vector2f(obstacleSize, obstacleSize), 0);
            obstacles.add(obstacle);
        });

        // @TODO check if objects penetrate each other

        return obstacles;
    }

    public void clearLevel() {

    }
}
