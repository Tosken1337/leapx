package de.leetgeeks.jgl.leapx.game.mechanic;

import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.util.GameDuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * @TODO Magnet effect which pulls the evading target to the player
 * Lwjgl
 * User: Sebastian
 * Date: 30.07.2015
 * Time: 14:11
 */
public class ObstacleMagnetComputer implements LevelStateComputer {
    private static final Logger log = LogManager.getLogger();

    private GameArena arena;
    private Level level;

    @Override
    public void init(Level level, GameArena arena) {
        this.level = level;
        this.arena = arena;
    }

    @Override
    public void update(GameDuration duration) {

    }

    @Override
    public void playerCollision(Obstacle obstacle) {

    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_F2 && action == 1) {
            final Vector2f playerPosition = level.getPlayer().getCenterPosition();

            level.getObstaclePhysx().stream()
                    .filter(obstaclePhysxBody -> obstaclePhysxBody.getPayload().isEvading())
                    .forEach(obstacle -> {
                        final Vector2f obstaclePosition = obstacle.getPayload().getCenterPosition();
                        final Vector2f diff = new Vector2f(playerPosition).sub(obstaclePosition);

                        /*final float distance = diff.length();
                        diff.normalize().mul(100 / distance);*/
                        diff.normalize().mul(10);

                        //@TODO should be update every frame

                        obstacle.getBody().applyLinearImpulse(new Vec2(diff.x, diff.y), new Vec2(obstaclePosition.x, obstaclePosition.y));

                    });
            //level.activateVisualHandicap(VisualHandicap.Schockwave);

            //level.switchVisualHandicap();
        }
    }
}
