package de.leetgeeks.jgl.leapx.game.object;

import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 13:37
 */
public class Player extends GameObject {
    private int score;

    public Player(Vector2f centerPosition, Vector2f dimension, float angle) {
        super(centerPosition, dimension, angle);
    }

    public void addToScore(int points) {
        score += points;
    }

    public String getScoreString() {
        return String.format("Score: %d", score);
    }
}
