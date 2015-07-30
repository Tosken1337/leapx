package de.leetgeeks.jgl.leapx.rendering;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.buffer.VertexArrayObject;
import de.leetgeeks.jgl.gl.buffer.VertexAttribBinding;
import de.leetgeeks.jgl.gl.buffer.VertexBufferObject;
import de.leetgeeks.jgl.gl.camera.OrthoCamera;
import de.leetgeeks.jgl.gl.font.HorizontalAlignment;
import de.leetgeeks.jgl.gl.font.TrueTypeFont;
import de.leetgeeks.jgl.gl.postprocessing.BasePostProcess;
import de.leetgeeks.jgl.gl.postprocessing.FxUniformProvider;
import de.leetgeeks.jgl.gl.postprocessing.FxUniformWrapper;
import de.leetgeeks.jgl.gl.postprocessing.PostProcessor;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.gl.texture.FrameBufferObject;
import de.leetgeeks.jgl.gl.texture.Texture;
import de.leetgeeks.jgl.gl.texture.TextureAttributes;
import de.leetgeeks.jgl.gl.texture.TextureCache;
import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Obstacle;
import de.leetgeeks.jgl.leapx.game.object.Player;
import de.leetgeeks.jgl.util.GameDuration;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.OpenGLException;

import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    private ShaderProgram backgroundShader;

    private TextureCache textureCache;

    private Texture playerTexture;
    private Texture backgroundTexture;
    private Texture backgroundTexture2;
    private List<Texture> obstacleTextures;
    private Map<Obstacle, Texture> obstacleTextureMap = new HashMap<>();

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

            //font = new TrueTypeFont("font/kenvector_future.ttf", 28f);
            font = new TrueTypeFont("font/computer_pixel-7.ttf", 42f);
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
        textureCache = new TextureCache();
        playerTexture = textureCache.get("/textures/ufoBlue.png");
        backgroundTexture = textureCache.get("/textures/background/black.png", new TextureAttributes(GL_LINEAR, GL_REPEAT));
        backgroundTexture2 = textureCache.get("/textures/background/galaxy.png", new TextureAttributes(GL_LINEAR, GL_REPEAT));
        obstacleTextures = new ArrayList<>();
        Files.list(Paths.get("resources/textures/obstacles")).forEach(path -> {
            try {
                obstacleTextures.add(textureCache.get("/textures/obstacles/" + path.getFileName().toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        GLHelper.checkAndThrow();
    }

    private void initShader() throws Exception {
        String vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/obstacleVert.glsl", this.getClass());
        String fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/obstacleFrag.glsl", this.getClass());
        obstacleShader = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);

        vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/obstacleVert.glsl", this.getClass());
        fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/playerFrag.glsl", this.getClass());
        playerShader = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);

        vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/backgroundVert.glsl", this.getClass());
        fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/backgroundFrag.glsl", this.getClass());
        backgroundShader = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);

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


    private void drawArena(final GameArena arena, double elapsedMillis) {
        // Draw background first
        backgroundTexture.bind(0);
        backgroundTexture2.bind(1);
        backgroundShader.use();

        final Matrix4f mat = new Matrix4f();
        final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        mat.identity();
        mat.translate(0, 0, -0.5f);
        mat.scale(arena.getWidth(), arena.getHeight(), 1);
        Matrix4f tmp = new Matrix4f(camera.getViewProjection()).mul(mat);
        tmp.get(matrixBuffer);
        backgroundShader.setUniformMatrixF("viewProjMatrix", matrixBuffer);
        backgroundShader.setUniformF("time", ((float) elapsedMillis));
        backgroundShader.setUniformTextureUnit("texImage", 0);
        backgroundShader.setUniformTextureUnit("texImage2", 1);
        backgroundShader.setUniformF("screenAspect", ((float) windowWidth / windowHeight));

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        backgroundTexture.unbind();
        backgroundTexture2.unbind();
    }

    private void drawObstacles(final List<Obstacle> obstacles, double elapsedMillis) {
        obstacleShader.use();

        final Matrix4f mat = new Matrix4f();
        final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        for (int i = 0; i < obstacles.size(); i++) {
            final Obstacle obstacle = obstacles.get(i);
            mat.identity();
            mat.translate(obstacle.getCenterPosition().x, obstacle.getCenterPosition().y, 0);
            mat.rotate(obstacle.getAngle(), 0, 0, 1);
            mat.scale(obstacle.getWidth(), obstacle.getHeight(), 1);

            Matrix4f tmp = new Matrix4f(camera.getViewProjection()).mul(coordinateRootTranslation).mul(mat);
            tmp.get(matrixBuffer);
            final int texId = i % obstacleTextures.size();
            obstacleTextureMap.computeIfAbsent(obstacle, o -> obstacleTextures.get(texId)).bind(0);
            obstacleShader.setUniformMatrixF("viewProjMatrix", matrixBuffer);
            obstacleShader.setUniformB("isEvading", obstacle.isEvading());
            obstacleShader.setUniformF("time", ((float) elapsedMillis));

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        }
    }

    private void drawPlayer(final Player player, double elapsedMillis) {
        playerTexture.bind(0);
        playerShader.use();

        final Matrix4f mat = new Matrix4f();
        final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

        mat.identity();
        mat.translate(player.getCenterPosition().x, player.getCenterPosition().y, 0);
        mat.rotate(player.getAngle(), 0, 0, 1);
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
        drawArena(arena, elapsedMillis);
        drawObstacles(gameLevel.getObstacles(), elapsedMillis);
        drawPlayer(gameLevel.getPlayer(), elapsedMillis);
        quadVao.unbind();

        fbo.unbind();

        final BasePostProcess fx = VisualHandicapPostFxFactory.getEffectFor(gameLevel.getVisualHandicap());
        fx.updateCustomUniforms(new EffectUniformProvider(gameLevel));
        postFx.addPostProcess(fx);
        postFx.drawOnScreenWithEffects(fbo.getColorAttachment(0).get(), 0, 0, windowWidth, windowHeight);


        if (gameLevel.isRunning()) {
            final GameDuration duration = gameLevel.getGameDuration();
            font.printOnScreen(50, 50, duration.toString(), windowWidth, windowHeight);
        } else {
            String displayString = "Pause - Put your Hand above the leap";
            font.printInScreen(HorizontalAlignment.Center, 0, 500, "Kirkwood Blaster", windowWidth, windowHeight);
            font.printInScreen(HorizontalAlignment.Center, 0, 540, displayString, windowWidth, windowHeight);
        }
        font.printInScreen(HorizontalAlignment.Right, 50f, 50f, gameLevel.getPlayer().getScoreString(), windowWidth, windowHeight);

        GLHelper.checkAndThrow();
    }

    /**
     *
     */
    private class EffectUniformProvider implements FxUniformProvider {
        private Level level;

        public EffectUniformProvider(final Level level) {
            this.level = level;
        }

        @Override
        public FxUniformWrapper<?> getUniformForName(String uniformName) {
            switch (uniformName) {
                case "focusPoint":
                    Vector2f playerPosition = level.getPlayer().getCenterPosition();
                    return new FxUniformWrapper<>(gameCoordinatesToScreen(playerPosition));

                case "playerPositionTexCoord":
                    playerPosition = level.getPlayer().getCenterPosition();
                    return new FxUniformWrapper<>(gameCoordinatesToScreenTextureSpace(playerPosition));

                case "time":
                    return new FxUniformWrapper<>(level.getTime());

                case "activationTime":
                    return new FxUniformWrapper<>(level.getVisualHandicap().getActivationTime());
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

        private Vector2f gameCoordinatesToScreenTextureSpace(final Vector2f coordinate) {
            float projectionWidth = camera.getRight() - camera.getLeft();
            float projectionHeight = camera.getTop() - camera.getBottom();
            float x = (coordinate.x / projectionWidth);
            float y = (coordinate.y / projectionHeight);

            return new Vector2f(x, y);
        }
    }
}
