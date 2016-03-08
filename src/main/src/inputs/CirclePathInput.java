package inputs;

import utilities.Point;

/**
 * Holds the user defined input for a circle path.
 * <p/>
 * Created by CyberPuck on 2016-03-01.
 */
public class CirclePathInput implements RobotInput {
    private InputMode mode = InputMode.PATH_CIRCLE;
    // Robot starting location
    private Point origin;
    // Radius of the circle
    private double radius;
    // inclination angle of the radius from the origin
    private double inclination;
    // end orientation of the robot
    private double endOrientation;
    // rate the robot needs to rotate to reach the end orientation
    private double rotationRate;
    // speed of the robot
    private double speed;
    // time to complete the circle
    private double time;
    // index for the path
    private int currentIndex;

    public CirclePathInput(Point origin, double radius, double inclination, double endOrientation, double rotationRate, double speed, double time) {
        this.origin = origin;
        this.radius = radius;
        this.inclination = inclination;
        this.endOrientation = endOrientation;
        this.rotationRate = rotationRate;
        this.speed = speed;
        this.time = time;
    }

    @Override
    public InputMode getMode() {
        return mode;
    }

    public double getEndOrientation() {
        return endOrientation;
    }

    public double getRotationRate() {
        return rotationRate;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getTime() {
        return time;
    }

    public Point getOrigin() {
        return origin;
    }

    public double getRadius() {
        return radius;
    }

    public double getInclination() {
        return inclination;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    @Override
    public String toString() {
        return "CirclePathInput{" +
                "mode=" + mode +
                ", origin=" + origin +
                ", radius=" + radius +
                ", inclination=" + inclination +
                ", endOrientation=" + endOrientation +
                ", rotationRate=" + rotationRate +
                ", speed=" + speed +
                ", time=" + time +
                '}';
    }
}
