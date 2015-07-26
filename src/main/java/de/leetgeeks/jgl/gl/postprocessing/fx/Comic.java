package de.leetgeeks.jgl.gl.postprocessing.fx;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.postprocessing.BasePostProcess;
import de.leetgeeks.jgl.gl.postprocessing.FxUniformProvider;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.util.ResourceUtil;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 19.07.2015
 * Time: 07:57
 */
public final class Comic extends BasePostProcess {
    public Comic() {
    }


    @Override
    public BasePostProcess enable() {
        return this;
    }

    @Override
    protected void initResources() {
        try {
            String vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/screenQuadVert.glsl", this.getClass());
            String fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/comicFrag.glsl", this.getClass());
            shaderProgram = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);
        } catch (Exception e) {
            throw new Error(e);
        }

        GLHelper.checkAndThrow();
    }

    @Override
    public void updateCustomUniforms(FxUniformProvider uniformProvider) {
        /*FxUniformWrapper<?> time = uniformProvider.getUniformForName("time");

        final double timeValue = ((Double) time.getUniformValue());
        System.out.println(timeValue);
        shaderProgram.use();
        shaderProgram.setUniformF("time", (float) timeValue);*/
    }
}
