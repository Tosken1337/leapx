package de.leetgeeks.jgl.application;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 27.06.2015
 * Time: 17:04
 */
public final class WinBaseApplication {
    private int width;
    private int height;
    private final boolean useFullscreen;
    private final ApplicationCallback callback;

    private static GLFWErrorCallback errorCallback;
    private static GLFWKeyCallback keyCallback;
    private static GLFWFramebufferSizeCallback fbCallback;

    private static long windowHandle;

    private boolean resized = false;

    private WinBaseApplication(int width, int height, boolean useFullscreen, ApplicationCallback callback) {
        this.width = width;
        this.height = height;
        this.useFullscreen = useFullscreen;
        this.callback = callback;
    }

    public void run() {
        try {
            init();
            loop();

            glfwDestroyWindow(windowHandle);
            keyCallback.release();
        } finally {
            glfwTerminate();
            errorCallback.release();
        }
    }

    private void loop() {
        GLContext.createFromCurrent();

        callback.onInitGl();

        // Remember the current time.
        long firstTime = System.nanoTime();

        while (glfwWindowShouldClose(windowHandle) == GL_FALSE) {
            // Build time difference between this and first time.
            long thisTime = System.nanoTime();
            double elapsedMillis = (thisTime - firstTime) / 1E6;

            if (resized) {
                callback.onResize(width, height);
                resized = false;
            }

            callback.onFrame(elapsedMillis);


            glfwSwapBuffers(windowHandle);
            glfwPollEvents();
        }

        callback.onRelease();
    }

    private void init() {
        glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
        if (glfwInit() != GL_TRUE)
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        windowHandle = glfwCreateWindow(width, height, "Application", MemoryUtil.NULL, MemoryUtil.NULL);
        if (windowHandle == MemoryUtil.NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(windowHandle, WinBaseApplication.keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key,
                               int scancode, int action, int mods) {
                if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                    glfwSetWindowShouldClose(window, GL_TRUE);
                callback.onKey(key, scancode, action, mods);
            }
        });
        glfwSetFramebufferSizeCallback(windowHandle,
                WinBaseApplication.fbCallback = new GLFWFramebufferSizeCallback() {
                    @Override
                    public void invoke(long window, int w, int h) {
                        if (w > 0 && h > 0) {
                            width = w;
                            height = h;
                            resized = true;
                        }
                    }
                });

        ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                windowHandle,
                (GLFWvidmode.width(vidmode) - width) / 2,
                (GLFWvidmode.height(vidmode) - height) / 2
        );

        callback.onInitNonGl();

        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(1);

        glfwShowWindow(windowHandle);
    }

    public final static class ApplicationBuilder {
        private int width;
        private int height;
        private boolean fullscreen;
        private ApplicationCallback callback;

        public ApplicationBuilder(ApplicationCallback callback) {
            this.callback = callback;
        }

        public ApplicationBuilder width(int width) {
            this.width = width;
            return this;
        }

        public ApplicationBuilder height(int height) {
            this.height = height;
            return this;
        }

        public ApplicationBuilder useFullscreen(boolean fullscreen) {
            this.fullscreen = fullscreen;
            return this;
        }

        public WinBaseApplication build() {
            return new WinBaseApplication(width, height, fullscreen, callback);
        }
    }
}
