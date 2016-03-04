package inputs;

import utilities.Point;

/**
 * Handles the end point control.
 * Created by CyberPuck on 2016-03-01.
 */
public class PointInput implements RobotInput {
    private InputMode mode = InputMode.POINT;

    // end point in feet
    private Point endPoint;
    // speed in feet/sec
    private double speed;
    private double endOrientation;
    private double time;

    public PointInput(Point endPoint, double speed, double endOrientation, double time) {
        this.endPoint = endPoint;
        this.endOrientation = endOrientation;
        this.time = time;
    }

    @Override
    public InputMode getMode() {
        return mode;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public double getSpeed() {
        return speed;
    }

    public double getEndOrientation() {
        return endOrientation;
    }

    public double getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "PointInput{" +
                "mode=" + mode +
                ", endPoint=" + endPoint +
                ", speed=" + speed +
                ", endOrientation=" + endOrientation +
                ", time=" + time +
                '}';
    }
}
