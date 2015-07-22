package de.leetgeeks.jgl.gl.postprocessing;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 19.07.2015
 * Time: 19:04
 */
public class FxUniformWrapper<T> {
    private T uniformValue;

    public FxUniformWrapper(T uniformValue) {
        this.uniformValue = uniformValue;
    }

    public T getUniformValue() {
        return uniformValue;
    }
}
