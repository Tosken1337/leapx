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
public class Twirl extends BasePostProcess {
    @Override
    public BasePostProcess enable() {
        return this;
    }

    @Override
    protected void initResources() {
        try {
            String vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/screenQuadVert.glsl", this.getClass());
            String fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/twirlFrag.glsl", this.getClass());
            shaderProgram = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);
        } catch (Exception e) {
            throw new Error(e);
        }

        GLHelper.checkAndThrow();
    }

    @Override
    public void updateCustomUniforms(FxUniformProvider uniformProvider) {
        FxUniformWrapper<?> focusPoint = uniformProvider.getUniformForName("focusPoint");
        FxUniformWrapper<?> time = uniformProvider.getUniformForName("time");
        FxUniformWrapper<?> activationTime = uniformProvider.getUniformForName("activationTime");
        final double timeValue = ((Double) time.getUniformValue());
        final double activationTimeValue = ((Double) activationTime.getUniformValue());
        final double deltaMillis = (timeValue - activationTimeValue) * 0.0008f;

        final Vector2f focus = ((Vector2f) focusPoint.getUniformValue());
        //final Vector2f center = new Vector2f(0.5f, 0.5f);
        shaderProgram.use();
        shaderProgram.setUniformF("time", (float) deltaMillis);
        shaderProgram.setUniformVector2f("centerPoint", focus);
    }
}
