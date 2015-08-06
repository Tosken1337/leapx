package de.leetgeeks.jgl.leapx.game.level.state;

import java.util.ArrayList;
import java.util.List;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 06.08.2015
 * Time: 20:14
 */
public class LevelStateMachine {
    private LevelState currentState;

    private List<LevelStateChangeListener> listeners;

    public LevelStateMachine(LevelState currentState) {
        this.currentState = currentState;
        listeners = new ArrayList<>();
    }

    public void addChangeListener(final LevelStateChangeListener listener) {
        listeners.add(listener);
    }

    private void fireStateChange(final LevelState newState) {
        LevelState oldState = currentState;
        currentState = newState;
        listeners.forEach(listener -> listener.stateChanged(oldState, currentState));
    }


    public void switchState(LevelState newState) {
        fireStateChange(newState);
    }

    public LevelState getState() {
        return currentState;
    }
}
