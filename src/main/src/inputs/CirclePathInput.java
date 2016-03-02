package inputs;

/**
 * Created by CyberPuck on 2016-03-01.
 */
public class CirclePathInput implements RobotInput {
    private InputMode mode = InputMode.PATH_CIRCLE;

    @Override
    public InputMode getMode() {
        return mode;
    }
}
