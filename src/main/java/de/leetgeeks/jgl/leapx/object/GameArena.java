package de.leetgeeks.jgl.leapx.object;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 21:15
 */
public class GameArena extends GameObject {
    private static final Logger log = LogManager.getLogger();
    private float arenaWidth;
    private float arenaHeight;

    public GameArena(float arenaWidth, float arenaHeight) {
        super(new Vector2f(arenaWidth / 2, arenaHeight / 2), new Vector2f(arenaWidth, arenaHeight), 0);
        this.arenaWidth = arenaWidth;
        this.arenaHeight = arenaHeight;
    }
}
