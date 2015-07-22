package de.leetgeeks.jgl.gl.postprocessing;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 19.07.2015
 * Time: 18:59
 */
public interface FxUniformProvider {
    FxUniformWrapper<?> getUniformForName(final String uniformName);
}
