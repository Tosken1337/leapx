package de.leetgeeks.jgl.leapx.rendering;

import de.leetgeeks.jgl.gl.GLHelper;
import de.leetgeeks.jgl.gl.buffer.VertexArrayObject;
import de.leetgeeks.jgl.gl.buffer.VertexAttribBinding;
import de.leetgeeks.jgl.gl.buffer.VertexBufferObject;
import de.leetgeeks.jgl.gl.camera.OrthoCamera;
import de.leetgeeks.jgl.gl.font.HorizontalAlignment;
import de.leetgeeks.jgl.gl.font.TrueTypeFont;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.gl.texture.Texture;
import de.leetgeeks.jgl.gl.texture.TextureCache;
import de.leetgeeks.jgl.leapx.game.level.Level;
import de.leetgeeks.jgl.leapx.game.object.GameArena;
import de.leetgeeks.jgl.leapx.game.object.Player;
import de.leetgeeks.jgl.util.GameDuration;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 30.07.2015
 * Time: 18:31
 */
public class UIRenderer {
    private static final Logger log = LogManager.getLogger();

    private int windowWidth;
    private int windowHeight;

    private OrthoCamera camera;
    private ShaderProgram uiShader;

    private TextureCache textureCache;

    private Texture playerTexture;


    private Matrix4f coordinateRootTranslation;

    private VertexArrayObject quadVao;

    private TrueTypeFont font;
    private TrueTypeFont fontBig;

    public void init() {
        try {
            initVertexBuffer();
            initShader();
            initTextures();

            font = new TrueTypeFont("font/computer_pixel-7.ttf", 42f);
            fontBig = new TrueTypeFont("font/computer_pixel-7.ttf", 80f);
            font.init();
            fontBig.init();
        } catch (Exception e) {
            log.error("Initialization failed!", e);
        }

        GLHelper.checkAndThrow();
    }

    private void initTextures() throws Exception {
        textureCache = new TextureCache();
        playerTexture = textureCache.get("/textures/ufoBlue.png");

        GLHelper.checkAndThrow();
    }

    private void initShader() throws Exception {
        String vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/screenQuadVert.glsl", this.getClass());
        String fragmentShaderSource = ResourceUtil.getResourceFileAsString("/shader/screenQuadFrag.glsl", this.getClass());
        uiShader = ShaderProgram.buildProgram(vertexShaderSource, fragmentShaderSource);

        GLHelper.checkAndThrow();
    }

    private void initVertexBuffer() {
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

        quadVao = VertexArrayObject.create(Arrays.asList(quadPosVbo, quadTexVbo));

        GLHelper.checkAndThrow();
    }

    public void windowResize(int width, int height, float orthoProjectionWidth, float orthoProjectionHeight) {
        windowWidth = width;
        windowHeight = height;
    }

    private void drawPlayerIcons(final Player player, double elapsedMillis) {
        quadVao.bind();
        playerTexture.bind(0);
        uiShader.use();

        final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        Matrix4f projectionMatrix = new Matrix4f().setOrtho(0, windowWidth, windowHeight, 0, -1, 1);

        for (int i = 0; i < player.numLives(); i++) {
            final Matrix4f mat = new Matrix4f();
            mat.identity();
            mat.translate((float)windowWidth - 100 - i * 60f, 60, 0);
            mat.translate(25, 25, 0);
            mat.scale(25, 25, 1);

            Matrix4f tmp = new Matrix4f(projectionMatrix).mul(mat);
            tmp.get(matrixBuffer);
            uiShader.setUniformMatrixF("projMatrix", matrixBuffer);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);
        }

        playerTexture.unbind();
        quadVao.unbind();
    }

    private void drawLabels(double elapsedMillis, GameArena arena, Level gameLevel) {

        switch (gameLevel.getState()) {

            case NotStarted:
                fontBig.printInScreen(HorizontalAlignment.Center, 0, 500, "Kirkwood Blaster", windowWidth, windowHeight);
                font.printInScreen(HorizontalAlignment.Center, 0, 540, "Get ready to rumble...", windowWidth, windowHeight);
                break;
            case Running:
                final GameDuration duration = gameLevel.getGameDuration();
                font.printOnScreen(50, 50, duration.toString(), windowWidth, windowHeight);
                break;
            case Paused:
                fontBig.printInScreen(HorizontalAlignment.Center, 0, 500, "Kirkwood Blaster", windowWidth, windowHeight);
                font.printInScreen(HorizontalAlignment.Center, 0, 540, "Pause - Place your Hand above the leap", windowWidth, windowHeight);
                break;
            case GameOver:
                fontBig.printInScreen(HorizontalAlignment.Center, 0, 500, "GameOver", windowWidth, windowHeight);
                fontBig.printInScreen(HorizontalAlignment.Center, 0, 540, "Your score: " + gameLevel.getPlayer().getScore(), windowWidth, windowHeight);
                break;
        }


        font.printInScreen(HorizontalAlignment.Right, 50f, 50f, gameLevel.getPlayer().getScoreString(), windowWidth, windowHeight);
    }

    public void onDraw(double elapsedMillis, GameArena arena, Level gameLevel) {
        glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_ENABLE_BIT);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        drawPlayerIcons(gameLevel.getPlayer(), elapsedMillis);

        drawLabels(elapsedMillis, arena, gameLevel);

        GLHelper.checkAndThrow();

        glPopAttrib();
    }
}

