package de.leetgeeks.jgl.leapx.game.mechanic;

import de.leetgeeks.jgl.leapx.game.command.EvadeCommand;
import de.leetgeeks.jgl.leapx.game.command.ObstacleCommand;
import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.util.GameDuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;

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
    public void compute(GameDuration duration) {
        if (lastEvadeEvent == null || lastEvadeEvent.plus(10).isOlderThan(duration)) {
            log.debug("Start evade");
            final List<PhysxBody<Obstacle>> obstacles = level.getObstaclePhysx();
            evadeCommand.execute(obstacles.get(rand.nextInt(obstacles.size())), duration);
            lastEvadeEvent = duration;
        }
    }
}
