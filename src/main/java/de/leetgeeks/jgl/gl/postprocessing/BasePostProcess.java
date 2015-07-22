package de.leetgeeks.jgl.gl.postprocessing;

import de.leetgeeks.jgl.gl.shader.ShaderProgram;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 18.07.2015
 * Time: 10:10
 */
public abstract class BasePostProcess {
    public final static String UNIFORM_INPUT_TEXTURE_SAMPLER_UNIT = "texImage";

    protected ShaderProgram shaderProgram;
    protected boolean initialized = false;

    public final BasePostProcess init() {

        initResources();
        initialized = true;
        return this;
    }

    public abstract BasePostProcess enable();

    protected abstract void initResources();

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    public abstract void updateCustomUniforms(final FxUniformProvider uniformProvider);

    /*public BasePostProcess setUniform2f(final String name, final Vector2f vec) {
        return this;
    }


    public BasePostProcess setUniform4f(final String name, final Vector3f vec) {
        return this;
    }

    public BasePostProcess setUniform4f(final String name, final Vector4f vec) {
        return this;
    }*/

    /*public BasePostProcess setInputTextureUnit(int textureUnit) {
        return this;
    }*/

    public boolean isInitialized() {
        return initialized;
    }
}
