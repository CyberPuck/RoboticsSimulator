package utilities;

/**
 * Has both a location as well as a rotation.
 *
 * Created by CyberPuck on 2016-02-28.
 */
public class Position {
    private Point position;
    private double angle;

    public Position() {}

    public Position(Point position, double angle) {
        this.position = position;
        this.angle = angle;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
