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
    private static double INPUT_LINE_WIDTH = 5.5;
    private static Paint INPUT_LINE_COLOR = Color.DARKVIOLET;
    private static Paint WAY_POINT_COLOR = Color.BLUE;
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
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        Utils.clearCanvas(gc, WIDTH, HEIGHT);
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
        robotPath.clear();
        clearCanvas();
    }

    /**
     * Clear the path canvas of all drawings.
     */
    public void clearCanvas() {
        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
        Utils.clearCanvas(gc, WIDTH, HEIGHT);
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
        // convert to pane coordinates
        Point paneLocation = Utils.convertToPaneCoordinates(origin, this.canvasCenter);
        // get a simple bounding box
        Point originLeft = new Point(paneLocation.getX() + 15, paneLocation.getY() + 15);
        Point originRight = new Point(paneLocation.getX() + 15, paneLocation.getY() - 15);
        Point originTop = new Point(paneLocation.getX() - 15, paneLocation.getY() - 15);
        Point originBottom = new Point(paneLocation.getX() - 15, paneLocation.getY() + 15);
        if (isInsideCanvas(originRight) || isInsideCanvas(originLeft) || isInsideCanvas(originTop) || isInsideCanvas(originBottom)) {
            gc.setFill(Color.YELLOW);
            gc.fillOval(paneLocation.getX() - 3.75, paneLocation.getY() - 3.75, 7.5, 7.5);
            gc.fillText("(0, 0)", paneLocation.getX() + 10, paneLocation.getY() + 20);
        }
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(1.0);
        // draw the origin lines
        if (this.canvasCenter.getY() - 360 < 0 && this.canvasCenter.getY() + 360 > 0) {
            // draw x axis
            Point x1 = Utils.convertToPaneCoordinates(new Point(this.canvasCenter.getX() - 180, 0), this.canvasCenter);
            Point x2 = Utils.convertToPaneCoordinates(new Point(this.canvasCenter.getX() + 180, 0), this.canvasCenter);
            gc.strokeLine(x1.getX(), x1.getY(), x2.getX(), x2.getY());
            gc.setFill(Color.YELLOW);
            gc.fillText("X-axis", 0, x1.getY());
        }
        if (this.canvasCenter.getX() - 180 < 0 && this.canvasCenter.getX() + 180 > 0) {
            //draw y axis
            Point y1 = Utils.convertToPaneCoordinates(new Point(0, this.canvasCenter.getY() - 360), this.canvasCenter);
            Point y2 = Utils.convertToPaneCoordinates(new Point(0, this.canvasCenter.getY() + 360), this.canvasCenter);
            gc.strokeLine(y1.getX(), y1.getY(), y2.getX(), y2.getY());
            gc.setFill(Color.YELLOW);
            gc.fillText("Y-axis", y1.getX(), 15);
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
        // Path might be offset from robot path, this is due to double precision inaccuracy
        Point temp;
        if (gi.getSpeed() >= 0) {
            temp = new Point(paneStartingPoint.getX(), paneStartingPoint.getY() + 1000000);
        } else {
            temp = new Point(paneStartingPoint.getX(), paneStartingPoint.getY() - 1000000);
        }
        Point paneEndPoint = Utils.convertToPaneCoordinates(temp, this.canvasCenter);
        Utils.rotate(gc, gi.getDirection() + 0.0000000001, paneStartingPoint);
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
        // check for way points
        drawWayPoints(pi.getWayPoints(), gc);
    }

    private void drawCirclePath(CirclePathInput input) {
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        gc.save();
        Point robotOrigin = Utils.convertLocationToPixels(input.getOrigin());
        double radius = input.getRadius() * 24;
        Point startingPoint = new Point(robotOrigin.getX() - radius, robotOrigin.getY() + 2 * radius);
        startingPoint = Utils.convertToPaneCoordinates(startingPoint, canvasCenter);
        Utils.rotate(gc, input.getInclination(), Utils.convertToPaneCoordinates(robotOrigin, this.canvasCenter));
        gc.setLineWidth(INPUT_LINE_WIDTH);
        gc.setStroke(INPUT_LINE_COLOR);
        gc.strokeOval(startingPoint.getX(), startingPoint.getY(), 2 * radius, 2 * radius);
        gc.restore();
        // check for way points
        drawWayPoints(input.getWayPoints(), gc);
    }

    private void drawRectanglePath(RectanglePathInput input) {
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        gc.save();
        Point robotOrigin = Utils.convertLocationToPixels(input.getOrigin());
        double topSide = input.getTopLength() * 24;
        double side = input.getSideLength() * 24;
        Point startingPoint = new Point(robotOrigin.getX(), robotOrigin.getY() + side);
        startingPoint = Utils.convertToPaneCoordinates(startingPoint, this.canvasCenter);
        Utils.rotate(gc, input.getInclination(), Utils.convertToPaneCoordinates(robotOrigin, this.canvasCenter));
        gc.setLineWidth(INPUT_LINE_WIDTH);
        gc.setStroke(INPUT_LINE_COLOR);
        gc.strokeRect(startingPoint.getX(), startingPoint.getY(), topSide, side);
        gc.restore();
        // check for way points
        drawWayPoints(input.getWayPoints(), gc);
    }

    private void drawFigureEightPath(FigureEightPathInput input) {
        GraphicsContext gc = this.pathCanvas.getGraphicsContext2D();
        gc.save();
        Point robotOrigin = Utils.convertLocationToPixels(input.getOrigin());
        double closeRadius = input.getRadiusOne() * 24;
        double farRadius = input.getRadiusTwo() * 24;
        Point closePoint = new Point(robotOrigin.getX() - closeRadius, robotOrigin.getY() + 2 * closeRadius);
        Point farPoint = new Point(robotOrigin.getX() - farRadius, robotOrigin.getY() + 2 * closeRadius + 2 * farRadius);
        closePoint = Utils.convertToPaneCoordinates(closePoint, this.canvasCenter);
        farPoint = Utils.convertToPaneCoordinates(farPoint, this.canvasCenter);
        Utils.rotate(gc, input.getInclination(), Utils.convertToPaneCoordinates(robotOrigin, this.canvasCenter));
        gc.setLineWidth(INPUT_LINE_WIDTH);
        gc.setStroke(INPUT_LINE_COLOR);
        gc.strokeOval(closePoint.getX(), closePoint.getY(), 2 * closeRadius, 2 * closeRadius);
        gc.strokeOval(farPoint.getX(), farPoint.getY(), 2 * farRadius, 2 * farRadius);
        gc.restore();
        // check for way points
        drawWayPoints(input.getWayPoints(), gc);
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

    /**
     * Attempts to draw way points on the canvas.
     *
     * @param wayPoints way points to draw
     * @param gc        graphics context
     */
    public void drawWayPoints(ArrayList<Point> wayPoints, GraphicsContext gc) {
        gc.setFill(WAY_POINT_COLOR);
        if (!wayPoints.isEmpty()) {
            // run though the array list
            for (int i = 0; i < wayPoints.size(); i++) {
                Point pixelPos = Utils.convertLocationToPixels(wayPoints.get(i));
                Point endPoint = Utils.convertToPaneCoordinates(pixelPos, this.canvasCenter);
                gc.fillOval(endPoint.getX(), endPoint.getY(), INPUT_LINE_WIDTH, INPUT_LINE_WIDTH);
            }
        }
    }
}
