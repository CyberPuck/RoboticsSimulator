package UI.displayCanvases;

import inputs.GeneralInput;
import inputs.InputMode;
import inputs.PointInput;
import inputs.RobotInput;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import utilities.Point;
import utilities.Utils;

import java.util.ArrayList;

/**
 * Handles drawing the paths overlay.
 * Created by CyberPuck on 2016-02-17.
 */
public class PathCanvas {
    private static double HEIGHT = 720;
    private static double WIDTH = 360;

    private Canvas pathCanvas;
    // preallocate arraylist, does this help with drawing bug?
    private ArrayList<Point> robotPath = new ArrayList<>(100000);
    private Point previousPosition;
    private Point canvasCenter;
    // origin of the display area, new requirement
    private Point origin = new Point(0, 0);
    // needed to draw the input path
    private Point startingLocation;
    private RobotInput input;

    public PathCanvas(Canvas pathCanvas) {
        this.pathCanvas = pathCanvas;
    }

    /**
     * Initializes the canvas with the canvas center position.
     *
     * @param position Canvas center in canvas reference frame
     */
    public void init(Point position) {
        startingLocation = position;
        previousPosition = position;
        robotPath.add(position);
        canvasCenter = position;
        // Make sure to try and draw the origin
        redrawOrigin();
    }

    public void setInput(RobotInput input) {
        this.input = input;
    }

    /**
     * Draws a user defined path.
     *
     * @param input Contains the path information to draw
     */
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

    /**
     * Clears the canvas and the existing robot's path.
     */
    public void restartCanvas() {
        clearCanvas();
        robotPath.clear();
    }

    /**
     * Clear the path canvas of all drawings.
     */
    public void clearCanvas() {
        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        redrawOrigin();
    }

    /**
     * Given a point in PIXEL coordinates, update the canvas center for drawing.
     *
     * @param newCenter new PIXEL center of the canvas
     */
    public void updateCenter(Point newCenter) {
        canvasCenter = newCenter;
        clearCanvas();
        // redraw the input path
        redrawInputPath();
        // redraw the path, need to only draw lines inside the canvas
        redrawRobotPath();
        // try to redraw origin if visible
        redrawOrigin();
    }

    /**
     * Checks if the origin is inside the draw plane, if so it is drawn.
     */
    private void redrawOrigin() {
        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
        Point originLeft = new Point(origin.getX() + 15, origin.getY() + 15);
        Point originRight = new Point(origin.getX() + 15, origin.getY() - 15);
        Point originTop = new Point(origin.getX() - 15, origin.getY() - 15);
        Point originBottom = new Point(origin.getX() - 15, origin.getY() + 15);
        if (isInsideCanvas(originRight) || isInsideCanvas(originLeft) || isInsideCanvas(originTop) || isInsideCanvas(originBottom)) {
            Point paneLocation = convertToPaneCoordinates(origin);
            gc.setFill(Color.ORANGE);
            gc.fillOval(paneLocation.getX() - 7.5, paneLocation.getY() - 7.5, 15, 15);
        }
    }

    /**
     * Redraws the robot path, useful if the canvas center was moved.
     */
    private void redrawRobotPath() {
        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
        for (int i = 1; i < robotPath.size(); i++) {
            if (isInsideCanvas(robotPath.get(i))) {
                Point oldPoint = convertToPaneCoordinates(robotPath.get(i - 1));
                Point newPoint = convertToPaneCoordinates(robotPath.get(i));
                // draw the line
                gc.setStroke(Color.LIME);
                gc.setLineWidth(0.0);
                gc.strokeLine(oldPoint.getX(), oldPoint.getY(), newPoint.getX(), newPoint.getY());
            }
        }
    }

    /**
     * Redraws the input defined path.  Make sure to draw BEFORE the robot path
     */
    public void redrawInputPath() {
        if (input.getMode() != InputMode.CONTROL_WHEELS) {
            switch (input.getMode()) {
                case CONTROL_GENERAL:
                    drawGeneralPath((GeneralInput) input);
                    break;
                case POINT:
                    drawPointPath((PointInput) input);
                    break;
                case PATH_CIRCLE:
                    break;
                case PATH_RECTANGLE:
                    break;
                case PATH_FIGURE_EIGHT:
                    break;
                default:
                    System.err.println(input.getMode() + " is not supported");
            }
        }
    }

    /**
     * Draws the general path, which is a straight line starting at the robot
     * starting going and going to infinity.
     *
     * @param gi General Input, provides data on the line slope
     */
    private void drawGeneralPath(GeneralInput gi) {
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        gc.save();
        Point paneStartingPoint = convertToPaneCoordinates(startingLocation);
        Point temp = new Point(paneStartingPoint.getX(), canvasCenter.getY() - 1000000);
        System.out.println("Starting point: " + paneStartingPoint.toString());
        System.out.println("New end point: " + temp.toString());
        Point paneEndPoint = convertToPaneCoordinates(temp);
        rotatePane(gc, gi.getDirection(), paneStartingPoint);
        gc.setLineWidth(4.0);
        gc.setStroke(Color.BLUE);
        gc.setFill(Color.BLUE);
        if (gi.getSpeed() != 0) {
            gc.strokeLine(paneStartingPoint.getX(), paneStartingPoint.getY(), paneEndPoint.getX(), paneEndPoint.getY());
        } else {
            gc.fillOval(paneStartingPoint.getX() - 2, paneStartingPoint.getY() - 2, 4, 4);
        }
        gc.restore();
    }

    private void drawPointPath(PointInput pi) {
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        gc.save();
        Point paneStartingPoint = convertToPaneCoordinates(startingLocation);
        Point pixelPos = Utils.convertLocationToPixels(pi.getEndPoint());
        Point endPoint = convertToPaneCoordinates(pixelPos);
        gc.save();
        gc.setLineWidth(4.0);
        gc.setStroke(Color.BLUE);
        gc.strokeLine(paneStartingPoint.getX(), paneStartingPoint.getY(), endPoint.getX(), endPoint.getY());
        gc.restore();
    }

    /**
     * Checks if a point in PIXEL coordinates is inside the canvas.
     *
     * @param point Point in PIXEL space to check
     * @return Flag indicating if the point is inside the canvas
     */
    private boolean isInsideCanvas(Point point) {
        if (point.getX() > this.canvasCenter.getX() - 180 && point.getX() < this.canvasCenter.getX() + 180 &&
                point.getY() > this.canvasCenter.getX() - 360 && point.getY() < this.canvasCenter.getY() + 360) {
            return true;
        }
        return false;
    }

    /**
     * Given a GRF based pixel coordinate, convert it to the canvas reference frame.
     *
     * @param globalLocation Global reference frame point (pixel coordinates)
     * @return Point in the canvas reference frame
     */
    private Point convertToPaneCoordinates(Point globalLocation) {
        double paneX = globalLocation.getX() - (this.canvasCenter.getX() - 180);
        double paneY = globalLocation.getY() - (this.canvasCenter.getY() - 360);
        paneY = 720 - paneY;
        return new Point(paneX, paneY);
    }

    /**
     * Rotate the canvas based on the angle given, and the provided pivot point.
     *
     * @param gc         GraphicsContext for rotating
     * @param angle      rotation angle
     * @param pivotPoint point to rotate around
     */
    private void rotatePane(GraphicsContext gc, double angle, Point pivotPoint) {
        // flip direction as it is backwards, and negate as it rotates in the wrong direction
        angle = (angle + 180) * -1;
        Rotate rotate = new Rotate(angle, pivotPoint.getX(), pivotPoint.getY());
        gc.transform(rotate.getMxx(), rotate.getMyx(), rotate.getMxy(), rotate.getMyy(), rotate.getTx(), rotate.getTy());
    }

    public void setStartingLocation(Point startingLocation) {
        this.startingLocation = startingLocation;
    }
}
