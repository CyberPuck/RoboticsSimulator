package utilities;

/**
 * Has both a location as well as a rotation.
 * TODO: Is position necessary?
 * <p/>
 * Created by CyberPuck on 2016-02-28.
 */
public class Position {
    private Point position;
    private double angle;

    public Position() {
        position = new Point(0, 0);
        angle = 0;
    }

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

    @Override
    public String toString() {
        return "Position{" +
                "position=" + position +
                ", angle=" + angle +
                '}';
    }
}
