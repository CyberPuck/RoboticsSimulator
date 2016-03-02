package inputs;

/**
 * Created by CyberPuck on 2016-03-01.
 */
public class RectanglePathInput implements RobotInput {
    private InputMode mode = InputMode.PATH_RECTANGLE;

    @Override
    public InputMode getMode() {
        return mode;
    }
}
