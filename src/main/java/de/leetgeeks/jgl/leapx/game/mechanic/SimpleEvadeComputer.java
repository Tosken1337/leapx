package de.leetgeeks.jgl.leapx.game.mechanic;

import de.leetgeeks.jgl.leapx.game.command.EvadeCommand;
import de.leetgeeks.jgl.leapx.game.command.ObstacleCommand;
import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.level.VisualHandicap;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.util.GameDuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 19.07.2015
 * Time: 08:04
 */
public class SimpleEvadeComputer implements LevelStateComputer {
    private static final Logger log = LogManager.getLogger();

    private Level level;
    private GameArena arena;

    private ObstacleCommand evadeCommand = new EvadeCommand();

    private GameDuration lastEvadeEvent;

    private final Random rand = new Random();

    @Override
    public void init(Level level, GameArena arena) {
        this.level = level;
        this.arena = arena;
    }

    @Override
    public void update(GameDuration duration) {
        if (lastEvadeEvent == null || lastEvadeEvent.plus(10).isOlderThan(duration)) {
            log.debug("Start evade at {}", duration);
            final List<PhysxBody<Obstacle>> obstacles = level.getObstaclePhysx().stream()
                    .filter(obstaclePhysxBody -> !obstaclePhysxBody.getPayload().isEvading())
                    .collect(Collectors.toList());
            evadeCommand.execute(obstacles.get(rand.nextInt(obstacles.size())), duration);
            lastEvadeEvent = duration;
        }
    }

    @Override
    public void playerCollision(Obstacle obstacle) {
        if (obstacle.isEvading()) {
            final GameDuration now = level.getGameDuration();
            final GameDuration evadeStart = obstacle.getEvadeStartTime();
            final long seconds = now.minus(evadeStart).seconds();
            final int scrore = Math.max(1200 - (int)seconds * 100, 150);
            log.debug("Obstacle caught after {} seconds. Scoring {} points", seconds, scrore);

            level.getPlayer().addToScore(scrore);
            obstacle.stopEvade();
            //level.deactivateVisualHandicap();
            level.activateVisualHandicap(VisualHandicap.Schockwave);
        } else {
            level.getPlayer().addToScore(-500);
            level.activateRandomVisualHandycap();
        }
    }
}
