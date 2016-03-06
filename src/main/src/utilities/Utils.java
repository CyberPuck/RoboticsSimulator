package utilities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Rotate;

import java.text.DecimalFormat;

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
     * @param grfPosition Robot coordinates in feet (GRF)
     * @return Position in pixels
     */
    public static Point convertLocationToPixels(Point grfPosition) {
        Point pixelLocation = new Point();
        // set the location
        pixelLocation.setX(grfPosition.getX() * 12 * 2);
        pixelLocation.setY(grfPosition.getY() * 12 * 2);
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

    /**
     * Simple Euclidean distance between two points.
     *
     * @param p1 first point
     * @param p2 second point
     * @return distance between p1 and p2
     */
    public static double distanceBetweenPoints(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.getX() + p2.getX(), 2) + Math.pow(p1.getY() + p2.getY(), 2));
    }

    /**
     * Given a point p1, see if it is within two inches of the gaol.
     *
     * @param p1   Point in question
     * @param goal Goal point
     * @return flag indicating if the goal has been reached
     */
    public static boolean isAtGoal(Point p1, Point goal) {
        double distance = 0.25;
        if (p1.getX() < goal.getX() + distance && p1.getX() > goal.getX() - distance
                && p1.getY() < goal.getY() + distance && p1.getY() > goal.getY() - distance) {
            return true;
        }
        return false;
    }

    /**
     * Given two points, calculate the angle between them based on the GRF.
     *
     * @param p1 first point
     * @param p2 second point
     * @return angle between the two based on 0 degrees being the +y-axis
     */
    public static double getAngle(Point p1, Point p2) {
        // add checks for 0, 180, 90, and 270 degrees
        if (p1.getX() == p2.getX()) {
            if (p1.getY() <= p2.getY()) {
                return 0.0;
            } else {
                return 180.0;
            }
        } else if (p1.getY() == p2.getY()) {
            if (p1.getX() > p2.getX()) {
                return 90.0;
            } else {
                return -90.0;
            }
        }
        // all angular calculations return radians (eww)
        return Math.toDegrees(Math.atan((p2.getY() - p1.getY()) / (p1.getX() - p2.getX())));
    }

    /**
     * Simply format a double to three decimals places, longer doubles are introducing error.
     *
     * @param value Original double value
     * @return double trimmed to the thousandths
     */
    public static double roundDouble(double value) {
        DecimalFormat df = new DecimalFormat("#.###");
        return Double.valueOf(df.format(value));
    }

    /**
     * Transform the given "graphics plane" at the given angle around the image (robot).
     *
     * @param gc         graphics context to draw the robot
     * @param angle      angle to rotate
     * @param pivotPoint Point to rotate around
     */
    public static void rotate(GraphicsContext gc, double angle, Point pivotPoint) {
        angle *= -1;
        Rotate rotate = new Rotate(angle, pivotPoint.getX(), pivotPoint.getY());
        gc.transform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
    }

    /**
     * Given a GRF based pixel coordinate, convert it to the canvas reference frame.
     *
     * @param globalLocation Global reference frame point (pixel coordinates)
     * @return Point in the canvas reference frame
     */
    public static Point convertToPaneCoordinates(Point globalLocation, Point origin) {
        double paneX = globalLocation.getX() - (origin.getX() - 180);
        double paneY = globalLocation.getY() - (origin.getY() - 360);
        paneY = 720 - paneY;
        return new Point(paneX, paneY);
    }
}
