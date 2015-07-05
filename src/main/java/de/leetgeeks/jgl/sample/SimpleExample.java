package de.leetgeeks.jgl.sample;

import com.leapmotion.leap.*;
import de.leetgeeks.jgl.application.ApplicationCallback;
import de.leetgeeks.jgl.application.WinBaseApplication;
import de.leetgeeks.jgl.gl.camera.PerspectiveCamera;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.physx.PhysxSimulation;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.jbox2d.common.Vec2;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 27.06.2015
 * Time: 17:05
 */
public class SimpleExample implements ApplicationCallback {
    private ShaderProgram shader;
    private PerspectiveCamera camera;
    private FloatBuffer viewProjectionBuffer = BufferUtils.createFloatBuffer(16);

    private Controller leapController;
    private Frame lastLeapFrame;

    private Matrix4f modelMatrix;

    private PhysxBody<Matrix4f> ballWithPayload;
    private PhysxSimulation physxSimulation;

    @Override
    public void onInitNonGl() {
        camera = new PerspectiveCamera(
                new Vector3f(0, 0, 3), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0),
                60f, 1280f / 800f, 0.01f, 100f);
        modelMatrix = new Matrix4f();
        modelMatrix.identity();

        leapController = new Controller();

        physxSimulation = PhysxSimulation.createWithWorld(new Vec2(0f, -10f));
        ballWithPayload = physxSimulation.createBallWithPayload(1f, new Vec2(0, 50), modelMatrix);
    }

    @Override
    public void onInitGl() {
        System.out.println("onInit");

        try {
            String vertexShaderSource = ResourceUtil.getResourceFileAsString("resource/vert.glsl", this.getClass());
            String fragmenShaderSource = ResourceUtil.getResourceFileAsString("resource/frag.glsl", this.getClass());

            shader = ShaderProgram.buildProgramm(vertexShaderSource, fragmenShaderSource);

        } catch (Exception e) {
            e.printStackTrace();
        }

        glClearColor(0.6f, 0.7f, 0.8f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    @Override
    public void onRelease() {
        System.out.println("onRelease");
    }

    @Override
    public void onFrame(double elapsedMillis) {
        processLeapInput();
        physxSimulation.simulate();
        //System.out.println(ballWithPayload);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.use();

        //camera.getViewProjection(viewProjectionBuffer);
        final Matrix4f mat = new Matrix4f(camera.getViewProjection()).mul(modelMatrix);
        mat.get(viewProjectionBuffer);
        shader.setUniformMatrixF("viewProjMatrix", viewProjectionBuffer);

        glBegin(GL_QUADS);
            glVertex3f(  0.5f, -0.5f, -0.5f );
            glVertex3f(  0.5f,  0.5f, -0.5f );
            glVertex3f( -0.5f,  0.5f, -0.5f );
            glVertex3f( -0.5f, -0.5f, -0.5f );
        glEnd();
    }

    @Override
    public void onResize(int width, int height) {
        System.out.println("onResize : " + width + "x" + height);
        //camera.setPerspective(60f, (float)width / (float)height);
        glViewport(0, 0, width, height);
    }

    private float scale = 1;
    private void processLeapInput() {
        final Frame frame = leapController.frame();
        // Update and process frame input data only if there is new data available
        if (lastLeapFrame == null || !lastLeapFrame.equals(frame)) {
            lastLeapFrame = lastLeapFrame == null ? frame : lastLeapFrame;

            // Process frame data
            /*float relativeScale = frame.scaleFactor(lastLeapFrame);
            this.scale -= (1 - relativeScale);
            System.out.println("Scale: " + scale);
            modelMatrix.identity();
            modelMatrix.scale(this.scale);*/


            InteractionBox iBox = frame.interactionBox();
            Pointable pointable = frame.pointables().frontmost();

            Vector leapPoint = pointable.stabilizedTipPosition();
            Vector normalizedPoint = iBox.normalizePoint(leapPoint, true);

            normalizedPoint = normalizedPoint.plus(new Vector(-0.5f, -0.5f, -0.5f));
            normalizedPoint = normalizedPoint.times(4);
            System.out.println("Normalized point: " + normalizedPoint);

            modelMatrix.identity();
            modelMatrix.translate(normalizedPoint.getX(), normalizedPoint.getY(), normalizedPoint.getZ());

            lastLeapFrame = frame;
        }
    }

    public static void main(String[] args) {
        final SimpleExample sampleApp = new SimpleExample();
        final WinBaseApplication app = new WinBaseApplication.ApplicationBuilder(sampleApp)
                .width(1280)
                .height(800)
                .build();
        app.run();
    }
}
