package UI.displayCanvases;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import robot.Robot;
import utilities.Point;
import utilities.Position;
import utilities.Utils;

/**
 * Handles drawing the robot.
 * Created by CyberPuck on 2016-02-17.
 */
public class RobotCanvas {
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
    // starting robot origin
    private Point robotOrigin;

    public RobotCanvas(Canvas robotCanvas, PathCanvas pathCanvas, Point origin) {
        // convert origin to canvas location
        origin = Utils.convertLocationToPixels(origin);
        Point paneOrigin = Utils.convertToPaneCoordinates(origin, origin);
        // setup canvas
        this.robotCanvas = robotCanvas;
        this.robotPosition = new Position(new Point(paneOrigin.getX(), paneOrigin.getY()), 0.0);
        // Import image
        this.robot = new Image(getClass().getResource("/assets/mecanum_robot_center.png").toString(), 48.0, 96.0, false, true);
        // Make the current center of the pane
        canvasCenter = new Point(origin.getX(), origin.getY());
        // setup the path canvas
        this.pathCanvas = pathCanvas;
        this.robotOrigin = origin;
    }

    /**
     * Setup the robot in the Global Reference Frame (0,0).
     */
    public void init() {
        GraphicsContext gc = robotCanvas.getGraphicsContext2D();
        // ensure the canvas is clear
        Utils.clearCanvas(gc, X_LENGTH, Y_LENGTH);
        Point robotPoint = Utils.convertToPaneCoordinates(robotOrigin, this.canvasCenter);
        gc.drawImage(robot, robotPoint.getX() - robot.getWidth() / 2, robotPoint.getY() - robot.getHeight() / 2);
        // initialize the path
        pathCanvas.init(robotOrigin);
    }

    /**
     * Draw the robot at its new position.
     */
    public void redrawRobot(Robot simRobot) {
        GraphicsContext gc = robotCanvas.getGraphicsContext2D();
        // clear the field first
        Utils.clearCanvas(gc, X_LENGTH, Y_LENGTH);
        // calculate the new location of the robot
        Point newPosition = Utils.convertLocationToPixels(simRobot.getLocation());
        // make sure we don't going running out of the boundary
        checkBoundaries(newPosition);
        Point panePoint = Utils.convertToPaneCoordinates(newPosition, this.canvasCenter);
        // set the robot position and angle
        this.robotPosition.setPosition(panePoint);
        this.robotPosition.setAngle(simRobot.getAngle());
        // draw the robot
        gc.save();
        // Update the robot location
        Utils.rotate(gc, this.robotPosition.getAngle(), this.robotPosition.getPosition());
        // convert coordinates to upper left of image, not robot center to draw it
        gc.drawImage(robot, panePoint.getX() - robot.getWidth() / 2, panePoint.getY() - robot.getHeight() / 2);
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

    public Position getGlobalPosition() {
        return Utils.convertLocationToFeet(this.robotPosition, this.canvasCenter);
    }
}
