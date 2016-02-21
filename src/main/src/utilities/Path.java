package utilities;

/**
 * Simple path class, useful for storing path end points.
 * Created by CyberPuck on 2016-02-17.
 */
public class Path {
    private Point start;
    private Point end;

    public Path(Point start) {
        this.start = start;
    }

    public Path(double x, double y) {
        start = new Point(x, y);
    }

    public void addEndPoint(Point end) {
        this.end = end;
    }

    public void addEndPoint(double x, double y) {
        end = new Point(x, y);
    }

    public Point getStart() {
        return start;
    }

    public void setStart(Point start) {
        this.start = start;
    }

    public Point getEnd() {
        return end;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return "Path{" +
                "start=" + start.toString() +
                ", end=" + end.toString() +
                '}';
    }
}