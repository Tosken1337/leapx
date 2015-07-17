package de.leetgeeks.jgl.leapx.input.leap;

import com.leapmotion.leap.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;

/**
 * Lwjgl
 * User: Sebastian
 * Date: 11.07.2015
 * Time: 13:42
 */
public class LeapInput {
    private static final Logger log = LogManager.getLogger();

    private Controller leapDevice;

    private Frame lastLeapFrame;

    private Vector lastPointableLocation;

    private Vector2f pointableLocationDelta;

    public LeapInput() {
        leapDevice = new Controller();
        lastPointableLocation = null;
        lastLeapFrame = null;
        pointableLocationDelta = new Vector2f(0, 0);
    }

    public Vector2f getPointableLocationDelta() {
        return pointableLocationDelta;
    }

    public void process() {
        pointableLocationDelta.set(0, 0);

        final Frame frame = leapDevice.frame();
        // Update and process frame input data only if there is new data available
        if (lastLeapFrame == null || !lastLeapFrame.equals(frame)) {
            lastLeapFrame = lastLeapFrame == null ? frame : lastLeapFrame;

            InteractionBox iBox = frame.interactionBox();
            Pointable pointable = frame.pointables().frontmost();

            if (pointable.isFinger()) {
                Vector leapPoint = pointable.stabilizedTipPosition();
                Vector normalizedPoint = iBox.normalizePoint(leapPoint, true);

                //log.trace("Normalized point: {}, pointable: {}", normalizedPoint, pointable);

                normalizedPoint = normalizedPoint.plus(new Vector(-0.5f, -0.5f, -0.5f));
                normalizedPoint = normalizedPoint.times(10);

                if (lastPointableLocation == null || lastPointableLocation.getY() == -5f) {
                    lastPointableLocation = normalizedPoint;
                }

                Vector diff = normalizedPoint.minus(lastPointableLocation).times(1);
                pointableLocationDelta.set(diff.getX(), diff.getY());

                lastPointableLocation = normalizedPoint;


                //box.getBody().applyForceToCenter(new Vec2(normalizedPoint.getX(), normalizedPoint.getY()));
                //box.getBody().applyForceToCenter(new Vec2(diff.getX(), diff.getY()));

                //playerBox.getBody().applyLinearImpulse(new Vec2(diff.getX(), diff.getY()), playerBox.getPosition());
                lastLeapFrame = frame;
            }
        }
    }
}
