package de.leetgeeks.jgl.leapx.rendering;

import de.leetgeeks.jgl.gl.postprocessing.BasePostProcess;
import de.leetgeeks.jgl.gl.postprocessing.fx.*;
import de.leetgeeks.jgl.leapx.game.level.VisualHandicap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumMap;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 19.07.2015
 * Time: 13:35
 */
public final class VisualHandicapPostFxFactory {
    private static final Logger log = LogManager.getLogger();
    private static final EnumMap<VisualHandicap, BasePostProcess> postFxRegistry = new EnumMap<>(VisualHandicap.class);

    static {
        postFxRegistry.put(VisualHandicap.None, new PassThroughPostProcess());
        postFxRegistry.put(VisualHandicap.Pixel, new Pixelate());
        postFxRegistry.put(VisualHandicap.Blur, new Blur());
        postFxRegistry.put(VisualHandicap.Edge, new Edge());
        postFxRegistry.put(VisualHandicap.Comic, new Comic());
        postFxRegistry.put(VisualHandicap.Schockwave, new Shockwave());
        postFxRegistry.put(VisualHandicap.Twirl, new Twirl());
    }

    private VisualHandicapPostFxFactory() {
    }

    public static BasePostProcess getEffectFor(final VisualHandicap handicap) {
        final BasePostProcess postProcess = postFxRegistry.get(handicap);
        // Lazy initialization
        if (!postProcess.isInitialized()) {
            log.debug("Initialize post process {}", postProcess);
            postProcess.init();
        }

        return postProcess;
    }
}
