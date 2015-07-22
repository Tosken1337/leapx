package de.leetgeeks.jgl.leapx.rendering;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.buffer.VertexArrayObject;
import de.leetgeeks.jgl.gl.buffer.VertexAttribBinding;
import de.leetgeeks.jgl.gl.buffer.VertexBufferObject;
import de.leetgeeks.jgl.gl.camera.OrthoCamera;
import de.leetgeeks.jgl.gl.font.TrueTypeFont;
import de.leetgeeks.jgl.gl.postprocessing.BasePostProcess;
import de.leetgeeks.jgl.gl.postprocessing.FxUniformProvider;
import de.leetgeeks.jgl.gl.postprocessing.FxUniformWrapper;
import de.leetgeeks.jgl.gl.postprocessing.PostProcessor;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.gl.texture.FrameBufferObject;
import de.leetgeeks.jgl.gl.texture.Texture;
import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.leapx.game.object.Player;
import de.leetgeeks.jgl.math.MathHelper;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.OpenGLException;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.lwjgl.opengl.GL11.*;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 13:37
 */
public class Renderer {
    private static final Logger log = LogManager.getLogger();

    private int windowWidth;
    private int windowHeight;

    private OrthoCamera camera;
    private ShaderProgram obstacleShader;
    private ShaderProgram playerShader;

    private Texture playerTexture;
    private Texture obstacleTexture;

    private Matrix4f coordinateRootTranslation;

    private VertexArrayObject quadVao;

    private FrameBufferObject fbo;

    private PostProcessor postFx;

    private TrueTypeFont font;

    public void init() {
        try {
            initVertexBuffer();
            initShader();
            initTextures();

            postFx = new PostProcessor();
            postFx.init();

            //font = new TrueTypeFont("font/invasion.ttf", 38f);
            font = new TrueTypeFont("font/ataris.ttf", 28f);
            font.init();
        } catch (Exception e) {
            log.error("Initialization failed!", e);
        }

        GLHelper.checkAndThrow();

        glClearColor(0.3f, 0.3f, 0.3f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    private void initBackBufferFbo(int width, int height) {
        if (fbo != null) {
            fbo.free();
        }

        fbo = FrameBufferObject.create();
        fbo.addColorAttachment(width, height, GL_RGBA8, 0).addDefaultDepthStencil(width, height);
        if (!fbo.isComplete()) {
            throw new OpenGLException("Unable to create fbo");
        }
        fbo.unbind();
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
        obstacleShader = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);

        vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/obstacleVert.glsl", this.getClass());
        fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/playerFrag.glsl", this.getClass());
        playerShader = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);

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
        VertexBufferObject quadPosVbo = VertexBufferObject.create(position);
        quadPosVbo.addAttribBinding(new VertexAttribBinding(VertexAttribBinding.POSITION_INDEX, 3));

        VertexBufferObject quadTexVbo = VertexBufferObject.create(texCoord);
        quadTexVbo.addAttribBinding(new VertexAttribBinding(VertexAttribBinding.TEX_COORD_INDEX, 2));

        //quadVao = VertexArrayObject.create(quadPosVbo);
        quadVao = VertexArrayObject.create(Arrays.asList(quadPosVbo, quadTexVbo));

        GLHelper.checkAndThrow();
    }

    public void windowResize(int width, int height, float orthoProjectionWidth, float orthoProjectionHeight) {
        log.trace("windowResize windowWidth {}, windowHeight {}, projectionWidth {}, projectionHeight {}", width, height, orthoProjectionWidth, orthoProjectionHeight);
        windowWidth = width;
        windowHeight = height;

        camera = new OrthoCamera(-orthoProjectionWidth / 2, orthoProjectionWidth / 2, orthoProjectionHeight / 2, -orthoProjectionHeight / 2, 0f, 1f);

        // All game objects are located in a coordinate system wit the root in the lower left corner, thus we need to translate them for rendering to the screen centered system of the renderer
        coordinateRootTranslation = new Matrix4f();
        coordinateRootTranslation.setTranslation(-orthoProjectionWidth / 2, -orthoProjectionHeight / 2, 0);

        // Rebuild fbo
        initBackBufferFbo(width, height);

        glViewport(0, 0, width, height);
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

    public void onDraw(double elapsedMillis, GameArena arena, Level gameLevel) {
        fbo.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        quadVao.bind();
        drawArena(arena);
        drawObstacles(gameLevel.getObstacles());
        drawPlayer(gameLevel.getPlayer());
        quadVao.unbind();

        fbo.unbind();

        final BasePostProcess fx = VisualHandicapPostFxFactory.getEffectFor(gameLevel.getVisualHandicap());
        fx.updateCustomUniforms(new EffectUniformProvider(gameLevel));
        postFx.addPostProcess(fx);
        postFx.drawOnScreenWithEffects(fbo.getColorAttachment(0).get(), 0, 0, windowWidth, windowHeight);

        String displayString = "Pause - Put your Hand above the leap to begin";
        if (gameLevel.isRunning()) {
            long millis = (long) gameLevel.getTime();
            displayString = String.format("%02d:%02d:%03d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)), millis - TimeUnit.MILLISECONDS.toSeconds(millis));
        }
        font.printOnScreen(50, 50, displayString, windowWidth, windowHeight);


        GLHelper.checkAndThrow();
    }

    private void test() {
        glDisable(GL_DEPTH_TEST);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(-1, 1, -1, 1, -1.0, 1.0);
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        GL20.glUseProgram(0);
        glColor4f(1, 0, 0, 1);
        glBegin(GL_QUADS);
        glVertex3f(0, 0.5f, 0.1f);
        glVertex3f(0, 0, 0.1f);
        glVertex3f(0.5f, 0, 0.1f);
        glVertex3f(0.5f, 0.5f, 0.1f);
        glEnd();
    }

    private class EffectUniformProvider implements FxUniformProvider {
        private Level level;

        public EffectUniformProvider(final Level level) {
            this.level = level;
        }

        @Override
        public FxUniformWrapper<?> getUniformForName(String uniformName) {
            switch (uniformName) {
                case "focusPoint":
                    final Vector2f playerPosition = level.getPlayer().getCenterPosition();
                    return new FxUniformWrapper<>(gameCoordinatesToScreen(playerPosition));

                case "time":
                    return new FxUniformWrapper<>(level.getTime());
            }

            return null;
        }

        private Vector2f gameCoordinatesToScreen(final Vector2f coordinate) {
            float projectionWidth = camera.getRight() - camera.getLeft();
            float projectionHeight = camera.getTop() - camera.getBottom();
            float x = (coordinate.x / projectionWidth) * windowWidth;
            float y = (coordinate.y / projectionHeight) * windowHeight;

            return new Vector2f(x, y);
        }
    }
}
