package UI.displayCanvases;

import inputs.RobotInput;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import utilities.Point;

import java.util.ArrayList;

/**
 * Handles drawing the paths overlay.
 * Created by CyberPuck on 2016-02-17.
 */
public class PathCanvas {
    private static double HEIGHT = 720;
    private static double WIDTH = 360;

    private Canvas pathCanvas;
    private ArrayList<Point> robotPath = new ArrayList<>();
    private Point previousPosition;
    private Point canvasCenter;
    // origin of the display area, new requirement
    private Point origin = new Point(0, 0);

    public PathCanvas(Canvas pathCanvas) {
        this.pathCanvas = pathCanvas;
    }

    public void init(Point position) {
        previousPosition = position;
        robotPath.add(position);
        canvasCenter = position;

        redrawOrigin();
    }

    public void drawPath(RobotInput input) {
        System.err.println("Not implemented!");
    }

    /**
     * Function to draw a path on the canvas
     */
    public void updateRobotPath(Point newLocation) {
        robotPath.add(newLocation);
        // TODO: fill this in, take into account relocation if the boundary is hit
        Point oldPoint = convertToPaneCoordinates(previousPosition);
        Point newPoint = convertToPaneCoordinates(newLocation);
        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
        gc.setStroke(Color.LIME);
        gc.setLineWidth(0);
        gc.strokeLine(oldPoint.getX(), oldPoint.getY(), newPoint.getX(), newPoint.getY());
        previousPosition = newLocation;
    }

    public void restartCanvas() {
        clearCanvas();
        robotPath.clear();
    }

    public void clearCanvas() {
        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        redrawOrigin();
    }

    public void updateCenter(Point newCenter) {
        canvasCenter = newCenter;
        clearCanvas();
        // redraw the path, need to only draw lines inside the canvas
        redrawPath();
        // try to redraw origin if visible
        redrawOrigin();
    }

    private void redrawOrigin() {
        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
        if (isInsideCanvas(origin)) {
            Point paneLocation = convertToPaneCoordinates(origin);
            gc.setFill(Color.ORANGE);
            gc.fillOval(paneLocation.getX(), paneLocation.getY(), 15, 15);
//            gc.strokeOval(paneLocation.getX(), paneLocation.getY(), 15, 15);
        }
    }

    private void redrawPath() {
        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
        for (int i = 1; i < robotPath.size(); i++) {
            if (isInsideCanvas(robotPath.get(i))) {
                Point oldPoint = convertToPaneCoordinates(robotPath.get(i - 1));
                Point newPoint = convertToPaneCoordinates(robotPath.get(i));
                // draw the line
                gc.setStroke(Color.LIME);
                gc.setLineWidth(0);
                gc.strokeLine(oldPoint.getX(), oldPoint.getY(), newPoint.getX(), newPoint.getY());
            }
        }
    }

    private boolean isInsideCanvas(Point point) {
        if (point.getX() > this.canvasCenter.getX() - 180 && point.getX() < this.canvasCenter.getX() + 180 &&
                point.getY() > this.canvasCenter.getX() - 360 && point.getY() < this.canvasCenter.getY() + 360) {
            return true;
        }
        return false;
    }

    private Point convertToPaneCoordinates(Point pixelLocation) {
        double paneX = pixelLocation.getX() - (this.canvasCenter.getX() - 180);
        double paneY = pixelLocation.getY() - (this.canvasCenter.getY() - 360);
        paneY = 720 - paneY;
        return new Point(paneX, paneY);
    }
}
