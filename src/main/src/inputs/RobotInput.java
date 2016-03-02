package inputs;

/**
 * Interface enabling handling of all inputs from the UI.  Its pretty simple just
 * makes it easier to pass data around.
 * <p/>
 * Created by CyberPuck on 2016-02-28.
 */
public interface RobotInput {
    /**
     * Get the mode of the input.
     *
     * @return Mode of the input
     */
    InputMode getMode();
}
