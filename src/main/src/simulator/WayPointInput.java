package simulator;

import utilities.Point;

import java.util.ArrayList;

/**
 * Stores way point input for the simulator.
 * <p/>
 * Created by CyberPuck on 2016-03-01.
 */
public class WayPointInput implements RobotInput {
    private InputMode mode = InputMode.WAY_POINTS;
    private Point endPoint;
    private ArrayList<Point> wayPoints;
    private double speed;
    private double rotationRate;
    private double time;

    @Override
    public InputMode getMode() {
        return mode;
    }
}
