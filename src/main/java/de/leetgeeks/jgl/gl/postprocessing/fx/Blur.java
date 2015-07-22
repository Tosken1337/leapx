package de.leetgeeks.jgl.gl.postprocessing.fx;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.postprocessing.BasePostProcess;
import de.leetgeeks.jgl.gl.postprocessing.FxUniformProvider;
import de.leetgeeks.jgl.gl.postprocessing.FxUniformWrapper;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 19.07.2015
 * Time: 14:32
 */
public class Blur extends BasePostProcess {
    @Override
    public BasePostProcess enable() {
        return this;
    }

    @Override
    protected void initResources() {
        try {
            String vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/screenQuadVert.glsl", this.getClass());
            String fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/blurFrag.glsl", this.getClass());
            shaderProgram = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);
        } catch (Exception e) {
            throw new Error(e);
        }

        GLHelper.checkAndThrow();
    }

    @Override
    public void updateCustomUniforms(FxUniformProvider uniformProvider) {
        FxUniformWrapper<?> focusPoint = uniformProvider.getUniformForName("focusPoint");

        final Vector2f focus = ((Vector2f) focusPoint.getUniformValue());
        shaderProgram.use();
        shaderProgram.setUniformVector2f("focusPoint", focus);
    }
}
