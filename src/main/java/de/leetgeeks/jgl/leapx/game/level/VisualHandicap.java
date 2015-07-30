package de.leetgeeks.jgl.leapx.game.level;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 19.07.2015
 * Time: 13:24
 */
public enum VisualHandicap {
    Blur,
    Edge,
    Pixel,
    Comic,
    Schockwave,
    None, Twirl;

    private double activationTime;

    public void setActivationTime(double activationTime) {
        this.activationTime = activationTime;
    }

    public double getActivationTime() {
        return activationTime;
    }
}
