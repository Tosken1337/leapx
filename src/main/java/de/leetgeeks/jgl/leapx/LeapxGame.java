package de.leetgeeks.jgl.leapx;

import de.leetgeeks.jgl.leapx.input.leap.LeapInput;
import de.leetgeeks.jgl.leapx.rendering.Renderer;
import de.leetgeeks.jgl.physx.PhysxSimulation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 13:35
 */
public class LeapxGame {
    private static final Logger log = LogManager.getLogger();

    private PhysxSimulation physxSimulator;

    private Renderer renderer;

    private LeapInput leap;

    public LeapxGame() {
    }
}
