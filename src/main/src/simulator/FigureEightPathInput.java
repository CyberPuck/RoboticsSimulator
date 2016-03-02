package simulator;

/**
 * Created by CyberPuck on 2016-03-01.
 */
public class FigureEightPathInput implements RobotInput {
    private InputMode mode = InputMode.PATH_FIGURE_EIGHT;

    private double speed;
    private double direction;
    private double rotationRate;

    public FigureEightPathInput(double direction, double speed, double rotationRate) {
        this.direction = direction;
        this.speed = speed;
        this.rotationRate = rotationRate;
    }

    @Override
    public InputMode getMode() {
        return mode;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDirection() {
        return direction;
    }

    public double getRotationRate() {
        return rotationRate;
    }

    @Override
    public String toString() {
        return "FigureEightPathInput{" +
                "mode=" + mode +
                ", speed=" + speed +
                ", direction=" + direction +
                ", rotationRate=" + rotationRate +
                '}';
    }
}
