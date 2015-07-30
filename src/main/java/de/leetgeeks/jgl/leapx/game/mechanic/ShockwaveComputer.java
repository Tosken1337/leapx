package de.leetgeeks.jgl.leapx.game.mechanic;

import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.level.VisualHandicap;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.util.GameDuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbox2d.common.Vec2;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 30.07.2015
 * Time: 14:11
 */
public class ShockwaveComputer implements LevelStateComputer {
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
        if (key == GLFW.GLFW_KEY_F1 && action == 1) {
            final Vector2f playerPosition = level.getPlayer().getCenterPosition();

            level.getObstaclePhysx().forEach(obstacle -> {
                final Vector2f obstaclePosition = obstacle.getPayload().getCenterPosition();
                final Vector2f diff = new Vector2f(obstaclePosition).sub(playerPosition);

                final float distance = diff.length();
                diff.normalize().mul(100 / distance);

                // @TODO apply diff as force
                obstacle.getBody().applyLinearImpulse(new Vec2(diff.x, diff.y), new Vec2(obstaclePosition.x, obstaclePosition.y));

            });
            level.activateVisualHandicap(VisualHandicap.Schockwave);

            //level.switchVisualHandicap();
        }
    }
}
