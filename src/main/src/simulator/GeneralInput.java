package simulator;

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
}
