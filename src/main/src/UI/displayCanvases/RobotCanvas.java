package UI.displayCanvases;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utilities.Point;

import java.io.File;

/**
 * Handles drawing the robot.
 * Created by CyberPuck on 2016-02-17.
 */
public class RobotCanvas {
    private static String ROBOT_IMAGE = "./assets/mecanum_robot_center.png";
    private Canvas robotCanvas;
    // location of robot, center of image
    private Point robotLocation;
    // angle of robot, from center of image
    private double robotAngle;

    public RobotCanvas(Canvas robotCanvas) {
        this.robotCanvas = robotCanvas;
        this.robotLocation = new Point(0, 600);
        this.robotAngle = 0;
    }

    /**
     * Setup the robot in the Global Reference Frame (0,0).
     */
    public void init() {
        File test = new File(ROBOT_IMAGE);
        System.out.println(test.isFile());
        Image robot = new Image("file:" + test.getAbsolutePath(), 48.0, 96.0, false, true);
        GraphicsContext gc = robotCanvas.getGraphicsContext2D();
        // draw the robot
        gc.drawImage(robot, robotLocation.getX(), robotLocation.getY());
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
