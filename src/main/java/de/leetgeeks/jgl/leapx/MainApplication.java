package de.leetgeeks.jgl.leapx;

import com.leapmotion.leap.*;
import de.leetgeeks.jgl.application.ApplicationCallback;
import de.leetgeeks.jgl.application.WinBaseApplication;
import de.leetgeeks.jgl.gl.camera.Camera;
import de.leetgeeks.jgl.gl.camera.OrthoCamera;
import de.leetgeeks.jgl.gl.shader.ShaderProgram;
import de.leetgeeks.jgl.physx.PhysxBody;
import de.leetgeeks.jgl.physx.PhysxSimulation;
import de.leetgeeks.jgl.util.ResourceUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbox2d.common.Vec2;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL11.*;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 27.06.2015
 * Time: 17:05
 */
public class MainApplication implements ApplicationCallback {
    private static final Logger log = LogManager.getLogger();

    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    private ShaderProgram shader;
    private Camera camera;
    private FloatBuffer viewProjectionBuffer = BufferUtils.createFloatBuffer(16);

    private Controller leapController;
    private Frame lastLeapFrame;

    private PhysxBody<Matrix4f> playerBox;
    private List<PhysxBody<Matrix4f>> obstacles = new ArrayList<>();
    private PhysxSimulation physxSimulation;

    @Override
    public void onInitNonGl() {
        final float halfWidth = 25f;
        final float halfHeight = halfWidth * (((float) HEIGHT) / WIDTH);
        camera = new OrthoCamera(-halfWidth, halfWidth, halfHeight, -halfHeight, 0f, 10f);

        leapController = new Controller();

        physxSimulation = PhysxSimulation.createWithWorld(new Vec2(0f, 0f));
        physxSimulation.setCollisionListener((bodyA, bodyB) -> {
            final boolean isPlayer = bodyA.equals(playerBox) || bodyB.equals(playerBox);
            if (isPlayer) {
                log.trace("Player collition");
            }
        });
        playerBox = physxSimulation.createRectangle(1f, 1f, new Vec2(0, 0), new Matrix4f(), true);

        final Random r = new Random();
        final float horizontalBound = halfWidth - 4;
        final float verticalBound = halfHeight - 4;
        for (int i = 0; i < 10; i++) {

            float x = r.nextFloat() * (2 * horizontalBound) - horizontalBound;
            float y = r.nextFloat() * (2 * verticalBound) - verticalBound;

            //log.trace("Creating obstacle at position x: " + x + ", y: " + y);

            final PhysxBody<Matrix4f> obstacle = physxSimulation.createRectangle(1f, 1f, new Vec2(x, y), new Matrix4f(), true);
            obstacles.add(obstacle);
        }


        physxSimulation.createRectangle(halfWidth * 2, 1f, new Vec2(0, -halfHeight), null, false);
        physxSimulation.createRectangle(halfWidth * 2, 1f, new Vec2(0, +halfHeight), null, false);
        physxSimulation.createRectangle(1, halfHeight * 2, new Vec2(-halfWidth, 0), null, false);
        physxSimulation.createRectangle(1, halfHeight * 2, new Vec2(halfWidth, 0), null, false);
    }

    @Override
    public void onInitGl() {
        try {
            String vertexShaderSource = ResourceUtil.getResourceFileAsString("/shader/vert.glsl", this.getClass());
            String fragmenShaderSource = ResourceUtil.getResourceFileAsString("/shader/frag.glsl", this.getClass());

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
    }

    @Override
    public void onFrame(double elapsedMillis) {
        processLeapInput();
        physxSimulation.simulate();

        // Update gl matrix with computed transformations
        updateObjects();

        // Draw obstacles and player box
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawObjects();
    }

    private void drawObjects() {
        shader.use();
        Stream.concat(obstacles.stream(), Stream.of(playerBox)).forEach(object -> {
            Matrix4f mat = object.getPayload();
            mat = new Matrix4f(camera.getViewProjection()).mul(mat);
            mat.get(viewProjectionBuffer);
            shader.setUniformMatrixF("viewProjMatrix", viewProjectionBuffer);
            drawQuad(1);
        });
    }

    private void updateObjects() {
        Stream.concat(obstacles.stream(), Stream.of(playerBox)).forEach(object -> {
            final Matrix4f m = object.getPayload();
            m.identity();
            m.translate(object.getPosition().x, object.getPosition().y, 0);
            m.rotate(((float) (object.getBody().getAngle() * Math.PI)), 0, 0, 1);
        });
    }

    private void drawQuad(float dimension) {
        final float halfWidth = dimension / 2f;
        glBegin(GL_QUADS);
        glVertex3f(  halfWidth, -halfWidth, 0 );
        glVertex3f(  halfWidth,  halfWidth, 0 );
        glVertex3f( -halfWidth,  halfWidth, 0 );
        glVertex3f( -halfWidth, -halfWidth, 0 );
        glEnd();
    }

    @Override
    public void onResize(int width, int height) {
        log.trace("onResize : " + width + "x" + height);
        final float halfWidth = 25;
        final float halfHeight = halfWidth * ((float)height / width);
        camera = new OrthoCamera(-halfWidth, halfWidth, halfHeight, -halfHeight, 0f, 10f);
        glViewport(0, 0, width, height);
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        if (key == GLFW.GLFW_KEY_RIGHT) {
            playerBox.getBody().applyLinearImpulse(new Vec2(2, 0), playerBox.getPosition());
        }
    }

    @Override
    public void onMouseMove(double x, double y) {
        //log.trace("x: {}, y: {}", x, y);
    }

    @Override
    public void onMouseButton(int button, int action, int mods) {
        //log.trace("Button: {}, Action: {}, Mods: {}", button, action, mods);
        switch (button) {
            case GLFW.GLFW_MOUSE_BUTTON_1:
                if (action == 1) {
                    log.trace("Button 1 pushed");
                } else {
                    log.trace("Button 1 released");
                }
                break;
        }
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


            if (pointable.isFinger()) {
                Vector leapPoint = pointable.stabilizedTipPosition();
                Vector normalizedPoint = iBox.normalizePoint(leapPoint, true);

                //log.trace("Normalized point: {}, pointable: {}", normalizedPoint, pointable);

                normalizedPoint = normalizedPoint.plus(new Vector(-0.5f, -0.5f, -0.5f));
                normalizedPoint = normalizedPoint.times(10);

                if (lastPenPosition == null || lastPenPosition.getY() == -5f) {
                    lastPenPosition = normalizedPoint;
                }

                Vector diff = normalizedPoint.minus(lastPenPosition).times(1);


                lastPenPosition = normalizedPoint;

                //box.getBody().applyForceToCenter(new Vec2(normalizedPoint.getX(), normalizedPoint.getY()));
                //box.getBody().applyForceToCenter(new Vec2(diff.getX(), diff.getY()));

                playerBox.getBody().applyLinearImpulse(new Vec2(diff.getX(), diff.getY()), playerBox.getPosition());

/*                normalizedPoint = normalizedPoint.plus(new Vector(-0.5f, -0.5f, -0.5f));
                normalizedPoint = normalizedPoint.times(30);*/

                //log.trace("Normalized point: {}, pointable: {}", normalizedPoint, pointable);

                /*MouseJointDef mouseJointDef = null;
                if (joint == null) {
                    mouseJointDef = new MouseJointDef();
                    mouseJointDef.bodyA = obstacles.get(0).getBody();
                    mouseJointDef.bodyB = playerBox.getBody();
                    mouseJointDef.target.set(-25, 0);
                    mouseJointDef.maxForce = playerBox.getBody().getMass() * 10;
                    mouseJointDef.collideConnected = true;
                    mouseJointDef.dampingRatio = 0.4f;
                    mouseJointDef.frequencyHz = 100;
                    joint = ((MouseJoint) physxSimulation.createJoint(mouseJointDef));
                    playerBox.getBody().setAwake(true);
                } else {
                    //joint.setTarget(new Vec2(25, 0));
                    joint.setTarget(new Vec2(normalizedPoint.getX(), normalizedPoint.getY()));
                }*/




                lastLeapFrame = frame;
            }
        }
    }

    private Vector lastPenPosition;

    public static void main(String[] args) {
        final MainApplication sampleApp = new MainApplication();
        final WinBaseApplication app = new WinBaseApplication.ApplicationBuilder(sampleApp)
                .width(WIDTH)
                .height(HEIGHT)
                .build();
        app.run();
    }
}