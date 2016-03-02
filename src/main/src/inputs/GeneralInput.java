package inputs;

import utilities.Point;

/**
 * This class handles the general inputs.
 * <p/>
 * Created by CyberPuck on 2016-02-29.
 */
public class GeneralInput implements RobotInput {
    private InputMode mode = InputMode.CONTROL_GENERAL;

    private double direction;
    private double speed;
    private double rotation;
    // Needed to calculate if the robot is off the path (mainly due to a rotation)
    private Point startLocation;

    public GeneralInput(double direction, double speed, double rotation) {
        this.direction = direction;
        this.speed = speed;
        this.rotation = rotation;
    }

    @Override
    public InputMode getMode() {
        return this.mode;
    }

    public double getDirection() {
        return direction;
    }

    public double getSpeed() {
        return speed;
    }

    public double getRotation() {
        return rotation;
    }

    public Point getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Point startLocation) {
        this.startLocation = startLocation;
    }

    @Override
    public String toString() {
        return "GeneralInput{" +
                "mode=" + mode +
                ", direction=" + direction +
                ", speed=" + speed +
                ", rotation=" + rotation +
                '}';
    }
}
