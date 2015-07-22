package de.leetgeeks.jgl.gl.postprocessing;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.buffer.VertexArrayObject;
import de.leetgeeks.jgl.gl.buffer.VertexAttribBinding;
import de.leetgeeks.jgl.gl.buffer.VertexBufferObject;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.gl.texture.Texture;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 18.07.2015
 * Time: 10:11
 */
public final class PostProcessor {
    /**
     *
     */
    private VertexArrayObject screenQuad;
    private ShaderProgram screenTextureProgram;

    private List<BasePostProcess> postEffects;

    public PostProcessor() {
        postEffects = new ArrayList<>();
    }

    public void init() throws Exception {
        float[] position = {
                // Left bottom triangle
                -1f, 1f, 0f,
                -1f, -1f, 0f,
                1f, -1f, 0f,
                // Right top triangle
                1f, -1f, 0f,
                1f, 1f, 0f,
                -1f, 1f, 0f
        };

        float[] texCoord = {
                // Left bottom triangle
                0f, 1f,
                0f, 0f,
                1f, 0f,
                // Right top triangle
                1f, 0f,
                1f, 1f,
                0f, 1f
        };
        VertexBufferObject quadPosVbo = VertexBufferObject.create(position);
        quadPosVbo.addAttribBinding(new VertexAttribBinding(VertexAttribBinding.POSITION_INDEX, 3));

        VertexBufferObject quadTexVbo = VertexBufferObject.create(texCoord);
        quadTexVbo.addAttribBinding(new VertexAttribBinding(VertexAttribBinding.TEX_COORD_INDEX, 2));

        screenQuad = VertexArrayObject.create(Arrays.asList(quadPosVbo, quadTexVbo));

        GLHelper.checkAndThrow();

        try {
            String vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/screenQuadVert.glsl", this.getClass());
            String fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/screenQuadFrag.glsl", this.getClass());
            screenTextureProgram = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);
        } catch (Exception e) {
            throw new Exception(e);
        }

        GLHelper.checkAndThrow();
    }

    /**
     * Adds the post process to the pipeline.
     * Currently we are support ign only one. @TODO Have to implement ping pong targets
     * @param postFx
     */
    public void addPostProcess(final BasePostProcess postFx) {
        postEffects.clear();
        postEffects.add(postFx);
    }

    public void drawOnScreenWithEffects(final Texture texture, int x, int y, int width, int height) {
        // currently supporting only one fx at a time
        final BasePostProcess postProcess = postEffects.get(0);
        drawOnScreen(texture, x, y, width, height, postProcess);

    }

    /**
     * Draw the texture in the default back buffer on the window coordinates passed to the function.
     * @param texture   The texture to draw.
     * @param x         Specify the x coordinate of lower left corner of the viewport rectangle, in pixels
     * @param y         Specify the y coordinate of lower left corner of the viewport rectangle, in pixels
     * @param width     The width of the viewport rectangle to draw the texture
     * @param height    The width of the viewport rectangle to draw the texture
     */
    public void drawOnScreen(final Texture texture, int x, int y, int width, int height, final BasePostProcess postfx) {
        // Save current viewport
        final IntBuffer viewportSize = BufferUtils.createIntBuffer(4);
        viewportSize.rewind();
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewportSize);

        // Create ortho projection matrix to render the screen quad
        Matrix4f projectionMatrix = new Matrix4f().setOrtho(-1, 1, -1, 1, -1, 1);

        // Set viewport to match the screen rectangle where the texture should be rendered
        GL11.glViewport(x, y, width, height);

        // Switch to default back buffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        projectionMatrix.get(matrixBuffer);

        screenQuad.bind();
        texture.bind();

        final ShaderProgram postFxProgram = postfx.getShaderProgram();

        postFxProgram.use();
        postFxProgram.setUniformMatrixF("projMatrix", matrixBuffer);
        postFxProgram.setUniformTextureUnit("texImage", 0);
        postFxProgram.setUniformVector2f("resolution", new Vector2f((float)width, (float)height));

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        screenQuad.unbind();
        texture.unbind();

        // Reset old viewport
        GL11.glViewport(viewportSize.get(0), viewportSize.get(1), viewportSize.get(2), viewportSize.get(3));
    }
}
