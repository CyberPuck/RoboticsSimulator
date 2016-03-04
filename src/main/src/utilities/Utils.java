package utilities;

import robot.Robot;

/**
 * General functions that are used by multiple classes.
 * <p/>
 * Created by CyberPuck on 2016-03-04.
 */
public class Utils {
    /**
     * Convert the simulation robot position (in feet), to the pane robot in pixels.
     * NOTE: 2 pixels = 1", 12 pixels = 6", 24 pixels = 1'
     *
     * @param simRobot Robot corrdinates in feet
     * @return Position in pixels
     */
    public static Position convertLocationToPixels(Robot simRobot) {
        Position pixelLocation = new Position();
        // set the location
        pixelLocation.getPosition().setX(simRobot.getLocation().getX() * 12 * 2);
        pixelLocation.getPosition().setY(simRobot.getLocation().getY() * 12 * 2);
        // set the angle
        pixelLocation.setAngle(simRobot.getAngle());
        return pixelLocation;
    }

    /**
     * Converts robot position in feet to the pixel location in the canvas reference frame.
     *
     * @param robotPosition  Global reference frame position
     * @param canvasLocation Center of the canvas the image is being drawn to
     * @return Position in the Canvas reference frame
     */
    public static Position convertLocationToFeet(Position robotPosition, Point canvasLocation) {
        System.out.println("Pixel Location: " + robotPosition.toString());
        double trueX = canvasLocation.getX() - 180 + robotPosition.getPosition().getX();
        double trueY = canvasLocation.getY() - 360 + robotPosition.getPosition().getY();
        // convert back to the global reference frame
        trueY = 720 - trueY;
        return new Position(new Point((trueX / (12 * 2)), (trueY / (12 * 2))), robotPosition.getAngle());
    }
}
