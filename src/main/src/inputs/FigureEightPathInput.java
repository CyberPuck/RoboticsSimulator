package inputs;

import utilities.Point;

/**
 * Holds the input for the figure eight path.
 * <p/>
 * Created by CyberPuck on 2016-03-01.
 */
public class FigureEightPathInput implements RobotInput {
    private InputMode mode = InputMode.PATH_FIGURE_EIGHT;

    // Origin (starting point) for the figure eight
    private Point origin;
    // radius of the closer circle for the figure eight
    private double radiusOne;
    // radius of the further circle for the figure eight
    private double radiusTwo;
    // angle both radii are at
    private double inclination;
    // speed robot can operate at
    private double speed;
    // end orientation of the robot
    private double endOrientation;
    // rotation rate of the vehicle
    private double rotationRate;
    // time to complete figure eight
    private double time;

    public FigureEightPathInput(Point origin, double radiusOne, double radiusTwo, double inclination, double endOrientation, double rotationRate, double speed, double time) {
        this.origin = origin;
        this.radiusOne = radiusOne;
        this.radiusTwo = radiusTwo;
        this.inclination = inclination;
        this.speed = speed;
        this.endOrientation = endOrientation;
        this.rotationRate = rotationRate;
        this.time = time;
    }

    @Override
    public InputMode getMode() {
        return mode;
    }

    public double getEndOrientation() {
        return endOrientation;
    }

    public Point getOrigin() {
        return origin;
    }

    public double getRadiusOne() {
        return radiusOne;
    }

    public double getRadiusTwo() {
        return radiusTwo;
    }

    public double getInclination() {
        return inclination;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getRotationRate() {
        return rotationRate;
    }

    public double getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "FigureEightPathInput{" +
                "mode=" + mode +
                ", origin=" + origin +
                ", radiusOne=" + radiusOne +
                ", radiusTwo=" + radiusTwo +
                ", inclination=" + inclination +
                ", speed=" + speed +
                ", endOrientation=" + endOrientation +
                ", rotationRate=" + rotationRate +
                ", time=" + time +
                '}';
    }
}
