package UI.displayCanvases;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import robot.Robot;
import utilities.Point;
import utilities.Position;
import utilities.Utils;

import java.io.File;

/**
 * Handles drawing the robot.
 * Created by CyberPuck on 2016-02-17.
 */
public class RobotCanvas {
    // robot file
    private static String ROBOT_IMAGE = "./src/main/src/assets/mecanum_robot_center.png";
    // static height of the view pane
    private static double X_LENGTH = 360;
    private static double Y_LENGTH = 720;
    // bounds to recenter
    private static double BOUNDARY_LENGTH_X = 108;
    private static double BOUNDARY_LENGTH_Y = 288;

    // Drawing canvas for the robot
    private Canvas robotCanvas;
    // location of robot, center of image
    private Position robotPosition;
    // image of the robot
    private Image robot;
    // the center of the pane (need to keep track so we know where the robot can move to)
    private Point canvasCenter;
    // Since this class handles shift the boundaries it handles the path canvas
    private PathCanvas pathCanvas;

    public RobotCanvas(Canvas robotCanvas, PathCanvas pathCanvas) {
        this.robotCanvas = robotCanvas;
        this.robotPosition = new Position(new Point(180, 360), 0.0);

        File robotImage = new File(ROBOT_IMAGE);
        this.robot = new Image("file:" + robotImage.getAbsolutePath(), 48.0, 96.0, false, true);
        // Make the current center of the pane
        canvasCenter = new Point(360 / 2, 720 / 2);
        // setup the path canvas
        this.pathCanvas = pathCanvas;
    }

    /**
     * Setup the robot in the Global Reference Frame (0,0).
     */
    public void init() {
        GraphicsContext gc = robotCanvas.getGraphicsContext2D();
        rotate(gc, 0, 180, 360);
        gc.drawImage(robot, 180 - robot.getWidth() / 2, 360 - robot.getHeight() / 2);
        // initialize the path
        pathCanvas.init(new Point(180, 360));
    }

    /**
     * Draw the robot at its new position.
     */
    public void redrawRobot(Robot simRobot) {
        GraphicsContext gc = robotCanvas.getGraphicsContext2D();
        // clear the field first
        gc.clearRect(0, 0, X_LENGTH, Y_LENGTH);
        // calculate the new location of the robot
        Point newPosition = Utils.convertLocationToPixels(simRobot.getLocation());
        // make sure we don't going running out of the boundary
        checkBoundaries(newPosition);
        double paneX = newPosition.getX() - (this.canvasCenter.getX() - 180);
        double paneY = newPosition.getY() - (this.canvasCenter.getY() - 360);
        // convert to the pane reference frame
        paneY = 720 - paneY;
        // set the robot position and angle
        this.robotPosition.setPosition(new Point(paneX, paneY));
        this.robotPosition.setAngle(simRobot.getAngle());
        // draw the robot
        gc.save();
        // Update the robot location
        // TODO: Remove these
//        System.out.println("Pixel Position: " + newPosition.toString());
//        System.out.println("ROBOT: " + this.robotPosition.toString());
//        System.out.println("Canvas Center: " + this.canvasCenter.toString());
        rotate(gc, this.robotPosition.getAngle(), paneX, paneY);
        // convert coordinates to upper left of image, not robot center to draw it
        gc.drawImage(robot, paneX - robot.getWidth() / 2, paneY - robot.getHeight() / 2);
        gc.restore();
        pathCanvas.updateRobotPath(newPosition);
    }



    /**
     * Checks if the pixel location is outside the boundary and update the canvas center
     * if needed.
     *
     * @param currentLocation Current pixel location of the robot.
     */
    private void checkBoundaries(Point currentLocation) {
        double x = currentLocation.getX();
        double y = currentLocation.getY();
        if (x > BOUNDARY_LENGTH_X + canvasCenter.getX() || x < canvasCenter.getX() - BOUNDARY_LENGTH_X) {
            System.out.println("Updating center, X");
            centerCanvas(currentLocation);
        }
        if (y > BOUNDARY_LENGTH_Y + canvasCenter.getY() || y < canvasCenter.getY() - BOUNDARY_LENGTH_Y) {
            System.out.println("Updating center, Y");
            centerCanvas(currentLocation);
        }

    }

    /**
     * Centers the canvas and updates the canvas center to take into account the
     * actual location of the robot in the state space.
     *
     * @param currentLocation pixel location of the robot
     * @return Center location
     */
    private void centerCanvas(Point currentLocation) {
        this.canvasCenter = currentLocation;
        // update the path canvas
        pathCanvas.updateCenter(this.canvasCenter);
    }

    /**
     * Transform the given "graphics plane" at the given angle around the image (robot).
     *
     * @param gc    graphics context to draw the robot
     * @param angle angle to rotate
     * @param x     position of the center of the robot image
     * @param y     position of the center of the robot image
     */
    private void rotate(GraphicsContext gc, double angle, double x, double y) {
//        angle += 180;
        Rotate rotate = new Rotate(angle, x, y);
        gc.transform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
    }

    public Position getGlobalPosition() {
        return Utils.convertLocationToFeet(this.robotPosition, this.canvasCenter);
    }
}
