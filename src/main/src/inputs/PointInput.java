package inputs;

import utilities.Point;

import java.util.ArrayList;

/**
 * Handles the end point control.
 * Created by CyberPuck on 2016-03-01.
 */
public class PointInput implements RobotInput {
    private InputMode mode = InputMode.POINT;

    // end point in feet
    private Point endPoint;
    // speed in feet/sec
    private double speed;
    // End orientation of the robot
    private double endOrientation;
    // based on the end orientation and time to complete
    private double rotationRate;
    // time to get the robot to the end point
    private double time;
    // Waypoint list
    private ArrayList<Point> wayPoints;

    public PointInput(Point endPoint, double speed, double endOrientation, double time, double rotationRate) {
        this.endPoint = endPoint;
        this.endOrientation = endOrientation;
        this.speed = speed;
        this.time = time;
        this.rotationRate = rotationRate;
    }

    @Override
    public InputMode getMode() {
        return mode;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getEndOrientation() {
        return endOrientation;
    }

    public double getTime() {
        return time;
    }

    public double getRotationRate() {
        return rotationRate;
    }

    public ArrayList<Point> getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(ArrayList<Point> wayPoints) {
        this.wayPoints = wayPoints;
    }

    @Override
    public String toString() {
        return "PointInput{" +
                "mode=" + mode +
                ", endPoint=" + endPoint +
                ", speed=" + speed +
                ", endOrientation=" + endOrientation +
                ", time=" + time +
                '}';
    }
}
