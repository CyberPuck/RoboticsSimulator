package inputs;

/**
 * Handles the wheel input mode from the UI.
 * <p/>
 * Created by CyberPuck on 2016-02-28.
 */
public class WheelInput implements RobotInput {
    private InputMode mode;

    private double wheelOne;
    private double wheelTwo;
    private double wheelThree;
    private double wheelFour;

    public WheelInput(double wheelOne, double wheelTwo, double wheelThree, double wheelFour) {
        this.wheelOne = wheelOne;
        this.wheelTwo = wheelTwo;
        this.wheelThree = wheelThree;
        this.wheelFour = wheelFour;
        mode = InputMode.CONTROL_WHEELS;
    }

    @Override
    public InputMode getMode() {
        return mode;
    }

    public double getWheelOne() {
        return wheelOne;
    }

    public double getWheelTwo() {
        return wheelTwo;
    }

    public double getWheelThree() {
        return wheelThree;
    }

    public double getWheelFour() {
        return wheelFour;
    }
}
