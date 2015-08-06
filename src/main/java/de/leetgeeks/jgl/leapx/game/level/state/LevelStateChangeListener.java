package de.leetgeeks.jgl.leapx.game.level.state;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 06.08.2015
 * Time: 20:15
 */
public interface LevelStateChangeListener {
    void stateChanged(LevelState oldState, LevelState newState);
}
