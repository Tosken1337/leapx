package de.leetgeeks.jgl.gl.texture.sprite;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.buffer.VertexArrayObject;
import de.leetgeeks.jgl.gl.buffer.VertexAttribBinding;
import de.leetgeeks.jgl.gl.buffer.VertexBufferObject;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.joml.Matrix4f;

import java.util.Arrays;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 04.08.2015
 * Time: 09:50
 */
public class SpriteSetManager {
    /**
     *
     */
    private VertexArrayObject spriteQuad;

    /**
     *
     */
    private ShaderProgram spriteProgram;


    public SpriteSetManager() {
    }

    public void playOnce(final String spriteName, final Matrix4f viewProjectionMatrix) {

    }

    public void play(final String spriteName, final Matrix4f viewProjectionMatrix) {

    }

    public void stop(final String spriteName) {

    }

    private void initResources() throws Exception {
        float[] position = {
                -1f, 1f, 0f,
                -1f, -1f, 0f,
                1f, -1f, 0f,

                1f, -1f, 0f,
                1f, 1f, 0f,
                -1f, 1f, 0f
        };

        float[] texCoord = {
                0f, 1f,
                0f, 0f,
                1f, 0f,

                1f, 0f,
                1f, 1f,
                0f, 1f
        };
        VertexBufferObject quadPosVbo = VertexBufferObject.create(position);
        quadPosVbo.addAttribBinding(new VertexAttribBinding(VertexAttribBinding.POSITION_INDEX, 3));

        VertexBufferObject quadTexVbo = VertexBufferObject.create(texCoord);
        quadTexVbo.addAttribBinding(new VertexAttribBinding(VertexAttribBinding.TEX_COORD_INDEX, 2));

        spriteQuad = VertexArrayObject.create(Arrays.asList(quadPosVbo, quadTexVbo));

        GLHelper.checkAndThrow();

        try {
            String vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/spriteVert.glsl", this.getClass());
            String fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/spriteFrag.glsl", this.getClass());
            spriteProgram = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);
        } catch (Exception e) {
            throw new Exception(e);
        }

        GLHelper.checkAndThrow();
    }
}
