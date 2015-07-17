package de.leetgeeks.jgl.leapx.rendering;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.buffer.VertexArrayObject;
import de.leetgeeks.jgl.gl.buffer.VertexAttribBinding;
import de.leetgeeks.jgl.gl.buffer.VertexBufferObject;
import de.leetgeeks.jgl.gl.camera.OrthoCamera;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.gl.texture.Texture;
import de.leetgeeks.jgl.leapx.object.GameArena;
import de.leetgeeks.jgl.leapx.object.Obstacle;
import de.leetgeeks.jgl.leapx.object.Player;
import de.leetgeeks.jgl.math.MathHelper;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 13:37
 */
public class Renderer {
    private static final Logger log = LogManager.getLogger();

    private OrthoCamera camera;
    private ShaderProgram obstacleShader;
    private ShaderProgram playerShader;

    private Texture playerTexture;
    private Texture obstacleTexture;

    private Matrix4f coordinateRootTranslation;

    private VertexArrayObject quadVao;
    private VertexBufferObject quadPosVbo;
    private VertexBufferObject quadTexVbo;

    public void init() {
        try {
            initVertexBuffer();
            initShader();
            initTextures();
        } catch (Exception e) {
            log.error("Initialization failed!", e);
        }

        GLHelper.checkAndThrow();

        glClearColor(0.6f, 0.7f, 0.8f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    private void initTextures() throws Exception {
        File texturePath = ResourceUtil.getResourceFile("/textures/spacestation.png", this.getClass());
        playerTexture = Texture.loadTexture(texturePath.toString());

        texturePath = ResourceUtil.getResourceFile("/textures/greenplanet.png", this.getClass());
        obstacleTexture = Texture.loadTexture(texturePath.toString());
        GLHelper.checkAndThrow();
    }

    private void initShader() throws Exception {
        String vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/obstacleVert.glsl", this.getClass());
        String fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/obstacleFrag.glsl", this.getClass());
        obstacleShader = ShaderProgram.buildProgramm(vertexShaderSource, fragmentShaderSource);

        vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/obstacleVert.glsl", this.getClass());
        fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/playerFrag.glsl", this.getClass());
        playerShader = ShaderProgram.buildProgramm(vertexShaderSource, fragmentShaderSource);

        GLHelper.checkAndThrow();
    }

    private void initVertexBuffer() {
        float[] position = {
                // Left bottom triangle
                -0.5f, 0.5f, 0f,
                -0.5f, -0.5f, 0f,
                0.5f, -0.5f, 0f,
                // Right top triangle
                0.5f, -0.5f, 0f,
                0.5f, 0.5f, 0f,
                -0.5f, 0.5f, 0f
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
        final FloatBuffer quadPos = BufferUtils.createFloatBuffer(6 * 3);
        quadPos.put(position);
        quadPos.flip();
        quadPosVbo = VertexBufferObject.create(quadPos);
        quadPosVbo.addAttribBinding(new VertexAttribBinding(VertexAttribBinding.POSITION_INDEX, 3));

        quadTexVbo = VertexBufferObject.create(texCoord);
        quadTexVbo.addAttribBinding(new VertexAttribBinding(VertexAttribBinding.TEX_COORD_INDEX, 2));

        //quadVao = VertexArrayObject.create(quadPosVbo);
        quadVao = VertexArrayObject.create(Arrays.asList(quadPosVbo, quadTexVbo));

        GLHelper.checkAndThrow();
    }

    public void windowResize(int width, int height, float orthoProjectionWidth, float orthoProjectionHeight) {
        log.trace("windowResize windowWidth {}, windowHeight {}, projectionWidth {}, projectionHeight {}", width, height, orthoProjectionWidth, orthoProjectionHeight);
        camera = new OrthoCamera(-orthoProjectionWidth / 2, orthoProjectionWidth / 2, orthoProjectionHeight / 2, -orthoProjectionHeight / 2, 0f, 1f);

        // All game objects are located in a coordinate system wit the root in the lower left corner, thus we need to translate them for rendering to the screen centered system of the renderer
        coordinateRootTranslation = new Matrix4f();
        coordinateRootTranslation.setTranslation(-orthoProjectionWidth / 2, -orthoProjectionHeight / 2, 0);

        glViewport(0, 0, width, height);
    }

    public void onDraw(double elapsedMillis, final GameArena arena, final List<Obstacle> obstacles, final Player player) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        drawArena(arena);

        GLHelper.checkAndThrow();
        quadVao.bind();
        drawObstacles(obstacles);
        drawPlayer(player);
        quadVao.unbind();
        GLHelper.checkAndThrow();

    }



    public void drawArena(final GameArena arena) {

    }

    public void drawObstacles(final List<Obstacle> obstacles) {

        obstacleTexture.bind(0);

        obstacleShader.use();

        // @TODO: perform multiplication of viewprojection and translation, rotation in shader
        final Matrix4f mat = new Matrix4f();
        final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        obstacles.forEach(object -> {
            mat.identity();
            mat.translate(object.getCenterPosition().x, object.getCenterPosition().y, 0);
            mat.rotate(((float) MathHelper.radToDeg(object.getAngle())), 0, 0, 1);
            mat.scale(object.getWidth(), object.getHeight(), 1);

            Matrix4f tmp = new Matrix4f(camera.getViewProjection()).mul(coordinateRootTranslation).mul(mat);
            tmp.get(matrixBuffer);
            obstacleShader.setUniformMatrixF("viewProjMatrix", matrixBuffer);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        });

        obstacleTexture.unbind();
    }

    public void drawPlayer(final Player player) {


        playerTexture.bind(0);
        playerShader.use();
        //playerShader.setUniformTextureUnit("texImage", 0);

        final Matrix4f mat = new Matrix4f();
        final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

        mat.identity();
        mat.translate(player.getCenterPosition().x, player.getCenterPosition().y, 0);
        mat.rotate(((float) MathHelper.radToDeg(player.getAngle())), 0, 0, 1);
        mat.scale(player.getWidth(), player.getHeight(), 1);

        Matrix4f tmp = new Matrix4f(camera.getViewProjection()).mul(coordinateRootTranslation).mul(mat);
        tmp.get(matrixBuffer);
        playerShader.setUniformMatrixF("viewProjMatrix", matrixBuffer);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        playerTexture.unbind();
    }
}
