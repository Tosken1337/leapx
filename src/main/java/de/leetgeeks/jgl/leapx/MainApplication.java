package de.leetgeeks.jgl.leapx;

import de.leetgeeks.jgl.application.ApplicationCallback;
import de.leetgeeks.jgl.application.WinBaseApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

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


    private LeapxGame game;

    @Override
    public void onInitNonGl() {
        final float halfWidth = 25f;
        final float halfHeight = halfWidth * (((float) HEIGHT) / WIDTH);

        game = new LeapxGame();
        game.init(2 * halfWidth, 2 * halfHeight);
    }

    @Override
    public void onInitGl() {
        game.initGlResources();
    }

    @Override
    public void onRelease() {
    }

    @Override
    public void onFrame(double elapsedMillis) {
        game.loop(elapsedMillis);
    }

    @Override
    public void onResize(int width, int height) {
        game.windowResize(width, height);
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        /*if (key == GLFW.GLFW_KEY_RIGHT) {
            playerBox.getBody().applyLinearImpulse(new Vec2(2, 0), playerBox.getPosition());
        }*/

        game.onKey(key, scancode, action, mods);
    }

    @Override
    public void onMouseMove(double x, double y) {
        //log.trace("x: {}, y: {}", x, y);
    }

    @Override
    public void onMouseButton(int button, int action, int mods) {
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


    public static void main(String[] args) {
        final MainApplication sampleApp = new MainApplication();
        final WinBaseApplication app = new WinBaseApplication.ApplicationBuilder(sampleApp)
                .width(WIDTH)
                .height(HEIGHT)
                .build();
        app.run();
    }
}
