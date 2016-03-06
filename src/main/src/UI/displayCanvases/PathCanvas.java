package UI.displayCanvases;

import inputs.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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
    private static double INPUT_LINE_WIDTH = 4.0;
    private static Paint INPUT_LINE_COLOR = Color.BLUE;
    private static double ROBOT_LINE_WIDTH = 0.0;
    private static Paint ROBOT_LINE_COLOR = Color.LIME;

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
        Point oldPoint = Utils.convertToPaneCoordinates(previousPosition, this.canvasCenter);
        Point newPoint = Utils.convertToPaneCoordinates(newLocation, this.canvasCenter);
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
        // forgot to convert to pane coordinates lol
//        Point origin = Utils.convertToPaneCoordinates(this.origin, this.canvasCenter);
        Point paneLocation = Utils.convertToPaneCoordinates(origin, this.canvasCenter);
        Point originLeft = new Point(paneLocation.getX() + 15, paneLocation.getY() + 15);
        Point originRight = new Point(paneLocation.getX() + 15, paneLocation.getY() - 15);
        Point originTop = new Point(paneLocation.getX() - 15, paneLocation.getY() - 15);
        Point originBottom = new Point(paneLocation.getX() - 15, paneLocation.getY() + 15);
        if (isInsideCanvas(originRight) || isInsideCanvas(originLeft) || isInsideCanvas(originTop) || isInsideCanvas(originBottom)) {
            gc.setFill(Color.YELLOW);
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
                Point oldPoint = Utils.convertToPaneCoordinates(robotPath.get(i - 1), this.canvasCenter);
                Point newPoint = Utils.convertToPaneCoordinates(robotPath.get(i), this.canvasCenter);
                // draw the line
                gc.setStroke(ROBOT_LINE_COLOR);
                gc.setLineWidth(ROBOT_LINE_WIDTH);
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
                    drawCirclePath((CirclePathInput) input);
                    break;
                case PATH_RECTANGLE:
                    drawRectanglePath((RectanglePathInput) input);
                    break;
                case PATH_FIGURE_EIGHT:
                    drawFigureEightPath((FigureEightPathInput) input);
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
        Point paneStartingPoint = Utils.convertToPaneCoordinates(startingLocation, this.canvasCenter);
        Point temp = new Point(paneStartingPoint.getX(), canvasCenter.getY() + 1000000);
        System.out.println("Starting point: " + paneStartingPoint.toString());
        System.out.println("New end point: " + temp.toString());
        System.out.println("Angle: " + gi.getDirection());
        Point paneEndPoint = Utils.convertToPaneCoordinates(temp, this.canvasCenter);
        Utils.rotate(gc, gi.getDirection(), paneStartingPoint);
        gc.setLineWidth(INPUT_LINE_WIDTH);
        gc.setStroke(INPUT_LINE_COLOR);
        gc.setFill(INPUT_LINE_COLOR);
        if (gi.getSpeed() != 0) {
            gc.strokeLine(paneStartingPoint.getX(), paneStartingPoint.getY(), paneEndPoint.getX(), paneEndPoint.getY());
        } else {
            gc.fillOval(paneStartingPoint.getX() - 2, paneStartingPoint.getY() - 2, 4, 4);
        }
        gc.restore();
    }

    /**
     * Draws a straight line path from the robot starting location to the input end point.
     *
     * @param pi input parameters for drawing the path
     */
    private void drawPointPath(PointInput pi) {
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        gc.save();
        Point paneStartingPoint = Utils.convertToPaneCoordinates(startingLocation, this.canvasCenter);
        Point pixelPos = Utils.convertLocationToPixels(pi.getEndPoint());
        Point endPoint = Utils.convertToPaneCoordinates(pixelPos, this.canvasCenter);
        gc.save();
        gc.setLineWidth(INPUT_LINE_WIDTH);
        gc.setStroke(INPUT_LINE_COLOR);
        gc.strokeLine(paneStartingPoint.getX(), paneStartingPoint.getY(), endPoint.getX(), endPoint.getY());
        gc.restore();
    }

    private void drawCirclePath(CirclePathInput input) {
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        gc.save();
        Point origin = Utils.convertLocationToPixels(input.getOrigin());
        double radius = input.getRadius() * 24;
        Point startingPoint = new Point(origin.getX() - radius, origin.getY() + 2 * radius);
        startingPoint = Utils.convertToPaneCoordinates(startingPoint, canvasCenter);
        Utils.rotate(gc, input.getInclination(), Utils.convertToPaneCoordinates(origin, this.canvasCenter));
        gc.setLineWidth(INPUT_LINE_WIDTH);
        gc.setStroke(INPUT_LINE_COLOR);
        gc.strokeOval(startingPoint.getX(), startingPoint.getY(), 2 * radius, 2 * radius);
        gc.restore();
    }

    private void drawRectanglePath(RectanglePathInput input) {
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        gc.save();
        Point origin = Utils.convertLocationToPixels(input.getOrigin());
        double topSide = input.getTopLength() * 24;
        double side = input.getSideLength() * 24;
        Point startingPoint = new Point(origin.getX(), origin.getY() + side);
        startingPoint = Utils.convertToPaneCoordinates(startingPoint, this.canvasCenter);
        Utils.rotate(gc, input.getInclination(), Utils.convertToPaneCoordinates(origin, this.canvasCenter));
        gc.setLineWidth(INPUT_LINE_WIDTH);
        gc.setStroke(INPUT_LINE_COLOR);
        gc.strokeRect(startingPoint.getX(), startingPoint.getY(), topSide, side);
        gc.restore();
    }

    private void drawFigureEightPath(FigureEightPathInput input) {
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        gc.save();
        Point origin = Utils.convertLocationToPixels(input.getOrigin());
        double closeRadius = input.getRadiusOne() * 24;
        double farRadius = input.getRadiusTwo() * 24;
        Point closePoint = new Point(origin.getX() - closeRadius, origin.getY() + 2 * closeRadius);
        Point farPoint = new Point(origin.getX() - farRadius, origin.getY() + 2 * closeRadius + 2 * farRadius);
        closePoint = Utils.convertToPaneCoordinates(closePoint, this.canvasCenter);
        farPoint = Utils.convertToPaneCoordinates(farPoint, this.canvasCenter);
        Utils.rotate(gc, input.getInclination(), Utils.convertToPaneCoordinates(origin, this.canvasCenter));
        gc.setLineWidth(INPUT_LINE_WIDTH);
        gc.setStroke(INPUT_LINE_COLOR);
        gc.strokeOval(closePoint.getX(), closePoint.getY(), 2 * closeRadius, 2 * closeRadius);
        gc.strokeOval(farPoint.getX(), farPoint.getY(), 2 * farRadius, 2 * farRadius);
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

    public void setStartingLocation(Point startingLocation) {
        this.startingLocation = startingLocation;
    }
}
