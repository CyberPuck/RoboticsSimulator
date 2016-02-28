package UI.displayCanvases;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import utilities.Point;

import java.io.File;

/**
 * Handles drawing the robot.
 * Created by CyberPuck on 2016-02-17.
 */
public class RobotCanvas {
    // robot file
    private static String ROBOT_IMAGE = "./assets/mecanum_robot_center.png";
    // static height of the view pane
    private static double X_LENGTH = 360;
    private static double Y_LENGTH = 720;
    // bounds to recenter
    private static double BOUNDARY_LENGTH = 72;

    private Canvas robotCanvas;
    // location of robot, center of image
    private Point robotLocation;
    // angle of robot, from center of image
    private double robotAngle;
    // image of the robot
    private Image robot;

    public RobotCanvas(Canvas robotCanvas) {
        this.robotCanvas = robotCanvas;
        this.robotLocation = new Point(300, 600);
        this.robotAngle = 0;
        File robotImage = new File(ROBOT_IMAGE);
        this.robot = new Image("file:" + robotImage.getAbsolutePath(), 48.0, 96.0, false, true);
    }

    /**
     * Setup the robot in the Global Reference Frame (0,0).
     */
    public void init() {
        redrawRobot();
    }

    /**
     * Draw the robot at its new position.
     */
    public void redrawRobot() {
        GraphicsContext gc = robotCanvas.getGraphicsContext2D();
        // clear the field first
        gc.clearRect(0,0,X_LENGTH, Y_LENGTH);
        double x = this.robotLocation.getX();
        double y = this.robotLocation.getY();
        // make sure we don't going running out of the boundary
        if(x + robot.getWidth()/2 < BOUNDARY_LENGTH || x + robot.getWidth()/2 > X_LENGTH - BOUNDARY_LENGTH) {
            this.robotLocation.setX(X_LENGTH/2);
            x = this.robotLocation.getX();
            this.robotLocation.setY(Y_LENGTH/2);
            y = this.robotLocation.getY();
        }
        if(y + robot.getHeight()/2 < BOUNDARY_LENGTH || y + robot.getHeight()/2 > Y_LENGTH - BOUNDARY_LENGTH) {
            this.robotLocation.setX(X_LENGTH/2);
            x = this.robotLocation.getX();
            this.robotLocation.setY(Y_LENGTH/2);
            y = this.robotLocation.getY();
        }
        // draw the robot
        gc.save();
        rotate(gc, robotAngle, x + robot.getWidth()/2, y + robot.getHeight()/2);
        gc.drawImage(robot, x, y);
        gc.restore();
    }

    /**
     * Transform the given "graphics plane" at the given angle around the image (robot).
     * @param gc graphics context to draw the robot
     * @param angle angle to rotate
     * @param x position of the center of the robot image
     * @param y position of the center of the robot image
     */
    private void rotate(GraphicsContext gc, double angle, double x, double y) {
        Rotate rotate = new Rotate(angle, x, y);
        gc.transform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
    }

    public double getRobotAngle() {
        return robotAngle;
    }

    public void setRobotAngle(double robotAngle) {
        this.robotAngle = robotAngle;
    }

    public Point getRobotLocation() {
        return robotLocation;
    }

    public void setRobotLocation(Point robotLocation) {
        this.robotLocation = robotLocation;
    }
}
