package simulator;

/**
 * Created by CyberPuck on 2016-03-01.
 */
public class PointInput implements RobotInput {
    private InputMode mode = InputMode.POINT;

    @Override
    public InputMode getMode() {
        return mode;
    }
}
