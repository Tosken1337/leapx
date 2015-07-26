package de.leetgeeks.jgl.leapx.game.command;

import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.util.GameDuration;
import org.jbox2d.common.Vec2;

import java.util.Random;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 23.07.2015
 * Time: 20:52
 */
public class EvadeCommand implements ObstacleCommand {
    private Random rand = new Random();

    @Override
    public void execute(PhysxBody<Obstacle> obstacle, GameDuration timestamp) {
        final float x = rand.nextFloat();
        final float y = rand.nextFloat();
        final Vec2 evadeVector = new Vec2(x, y);
        evadeVector.normalize();
        obstacle.getBody().setLinearVelocity(evadeVector.mul(rand.nextFloat() * 10 + 5));
        obstacle.getPayload().startEvade(true, timestamp);
    }
}
