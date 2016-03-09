package inputs;

import utilities.Point;

/**
 * Holds input data for the rectangle path.
 * <p/>
 * Created by CyberPuck on 2016-03-01.
 */
public class RectanglePathInput implements RobotInput {
    private InputMode mode = InputMode.PATH_RECTANGLE;

    // origin of the rectangle (a vertex)
    private Point origin;
    // length of the top and bottom line segments of the rectangle
    private double topLength;
    // length of the both side line segments of the the rectangle
    private double sideLength;
    // angle of the rectangle in relation to the origin
    private double inclination;
    // end orientation of the robot
    private double endOrientation;
    // rate robot rotates while moving along path
    private double rotationRate;
    // time to complete rectangle path
    private double time;

    public RectanglePathInput(Point origin, double topLength, double sideLength, double inclination, double endOrientation, double rotationRate, double time) {
        this.origin = origin;
        this.topLength = topLength;
        this.sideLength = sideLength;
        this.inclination = inclination;
        this.endOrientation = endOrientation;
        this.rotationRate = rotationRate;
        this.time = time;
    }

    @Override
    public InputMode getMode() {
        return mode;
    }

    public Point getOrigin() {
        return origin;
    }

    public double getTopLength() {
        return topLength;
    }

    public double getSideLength() {
        return sideLength;
    }

    public double getInclination() {
        return inclination;
    }

    public double getEndOrientation() {
        return endOrientation;
    }

    public double getRotationRate() {
        return rotationRate;
    }

    public double getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "RectanglePathInput{" +
                "mode=" + mode +
                ", origin=" + origin +
                ", topLength=" + topLength +
                ", sideLength=" + sideLength +
                ", inclination=" + inclination +
                ", endOrientation=" + endOrientation +
                ", rotationRate=" + rotationRate +
                ", time=" + time +
                '}';
    }
}
