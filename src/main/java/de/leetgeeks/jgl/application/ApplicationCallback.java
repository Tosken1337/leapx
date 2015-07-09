package de.leetgeeks.jgl.application;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 27.06.2015
 * Time: 17:10
 */
public interface ApplicationCallback {
    void onInitNonGl();
    void onInitGl();
    void onRelease();
    void onFrame(double elapsedMillis);
    void onResize(int width, int height);

    void onKey(int key, int scancode, int action, int mods);
}
