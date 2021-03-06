package de.leetgeeks.jgl.leapx.game.mechanic;

import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.util.GameDuration;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 23.07.2015
 * Time: 21:19
 */
public interface LevelStateComputer {
    void init(Level level, GameArena arena);
    void update(GameDuration duration);

    void playerCollision(Obstacle obstacle);
    void onKey(int key, int scancode, int action, int mods);
}
