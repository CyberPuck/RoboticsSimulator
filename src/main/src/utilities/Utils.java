package utilities;

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
}
