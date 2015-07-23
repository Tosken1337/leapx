package de.leetgeeks.jgl.leapx.game.command;

import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.util.GameDuration;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 23.07.2015
 * Time: 20:50
 */
public interface ObstacleCommand {
    void execute(PhysxBody<Obstacle> obstacle, GameDuration timestamp);
}
