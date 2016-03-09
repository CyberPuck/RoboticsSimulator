package simulator;

import inputs.*;
import robot.Kinematics;
import robot.Robot;
import robot.VelocityEquations;
import utilities.Point;
import utilities.Position;
import utilities.Utils;

import java.util.ArrayList;

/**
 * This is the simulator class, like highlander there can be only one.
 * Handles: path planning, movement, and conversion to canvas coordinates.
 *
 * <p/>
 * Created by CyberPuck on 2016-02-26.
 */
public class Simulator {
    // robot basics
    private static double ROBOT_LENGTH = 2;
    private static double ROBOT_HEIGHT = 4;
    // every 100 ms recalculate the course to take
    private static double RECALCULATE_COURSE = 0.1;
    // distance to start slowing down
    private static double SLOW_DOWN_DISTANCE = 1.0;
    //radius of the wheels
    private double wheelRadius;
    // time since the last velocity and rotation rate recalculation
    private double lastRecalculation;
    // handle on the data to move the robot
    private Robot robot;
    // current input for the robot to move
    private RobotInput input;
    // flag indicating if the goal has been reached
    private boolean atGoal;
    // flag indicating if the simulation can complete in time
    private boolean enoughTime;
    // Array list of points for complex paths
    private ArrayList<Point> pathVertices = new ArrayList<>();
    // index in the path list
    private int pathIndex;
    // max/desired speed of the robot
    private double speed;

    public Simulator(RobotInput input, Robot robot, double wheelRadius) {
        this.input = input;
        this.robot = robot;
        enoughTime = initializeRobot(input, this.robot);
        atGoal = false;
        this.wheelRadius = wheelRadius;
    }

    /**
     * Used to get the current robot information.
     *
     * @return Current robot state in reference to the robot frame
     */
    public Robot getRobot() {
        return robot;
    }

    /**
     * Flag indicating if the robot got to the goal in time
     *
     * @return goal reached?
     */
    public boolean isAtGoal() {
        return atGoal;
    }

    /**
     * Flag indicating if the robot can get to the goal before time runs out.
     *
     * @return reach goal in time?
     */
    public boolean isEnoughTime() {
        return enoughTime;
    }

    /**
     * Due to different input classes having different features make sure input variables are added.
     *
     * @param input Input of the robot
     * @param robot Robot to update
     */
    private boolean initializeRobot(RobotInput input, Robot robot) {
        double distance = 0.0;
        pathIndex = 0;
        switch (input.getMode()) {
            case CONTROL_WHEELS: // Not needed all four wheel rates are read later
                break;
            case CONTROL_GENERAL:
                robot.setRotationRate(((GeneralInput) input).getRotation());
                break;
            case POINT:
                PointInput pi = (PointInput) input;
                distance = calculatePointDistance(pi);
                if (!verifyPathCompletion(pi.getTime(), distance)) {
                    return false;
                }
                robot.setRotationRate(pi.getRotationRate());
                speed = distance / pi.getTime();
                break;
            case PATH_RECTANGLE:
                RectanglePathInput rpi = (RectanglePathInput) input;
                distance = calculateRectangleDistance(rpi);
                if (!verifyPathCompletion(rpi.getTime(), distance)) {
                    return false;
                }
                robot.setRotationRate(rpi.getRotationRate());
                speed = distance / rpi.getTime();
                break;
            case PATH_CIRCLE:
                CirclePathInput cpi = (CirclePathInput) input;
                distance = calculateCircleDistance(cpi);
                if (!verifyPathCompletion(cpi.getTime(), distance)) {
                    return false;
                }
                // generate the path
                robot.setRotationRate(cpi.getRotationRate());
                speed = distance / cpi.getTime();
                break;
            case PATH_FIGURE_EIGHT:
                FigureEightPathInput fepi = (FigureEightPathInput) input;
                distance = calculateFigureEightPath(fepi);
                if (!verifyPathCompletion(fepi.getTime(), distance)) {
                    return false;
                }
                // generate the path
                robot.setRotationRate(fepi.getRotationRate());
                speed = distance / fepi.getTime();
                break;
            default:
                System.err.println("Not implemented");
        }
        this.input = input;
        return true;
    }

    public void setRobot(Robot robot) {
        lastRecalculation = 0.0;
        this.robot = robot;
    }

    /**
     * Given the defined input, calculate the new position of the robot.
     *
     * @param timeDelta Time difference between this calculation and the last one
     */
    public void calculateNewPosition(double timeDelta) {
        lastRecalculation += timeDelta;
        // calculate the course
        if (lastRecalculation > RECALCULATE_COURSE) {
            // reset last recalculation time
            lastRecalculation = 0.0;
            // update robot velocity settings to course correct
            switch (input.getMode()) {
                case CONTROL_WHEELS:
                    calculateWheelMovement(input, timeDelta);
                    break;
                case CONTROL_GENERAL:
                    calculateGeneralMovement(input, timeDelta);
                    break;
                case POINT:
                    calculatePointMovement(input, timeDelta);
                    break;
                case PATH_RECTANGLE:
                    calculateRectangleMovement(input, timeDelta);
                    break;
                case PATH_CIRCLE:
                    calculateCircleMovement(input, timeDelta);
                    break;
                case PATH_FIGURE_EIGHT:
                    calculateFigureEightMovement(input, timeDelta);
                    break;
                default:
                    System.err.println("Not implemented");
            }
        }
        // update the robot position
        updateRobot(robot.getVelocity().getX(), robot.getVelocity().getY(), robot.getRotationRate(), timeDelta, robot);
    }

    /**
     * Based on user input and the time difference calculate the new location, Vs, and angle.
     *
     * @param input     User input for wheel rotations
     * @param timeDelta Difference between last calculation
     */
    private void calculateWheelMovement(RobotInput input, double timeDelta) {
        WheelInput wInput = (WheelInput) input;
        // Get the wheel rates
        double w1 = wInput.getWheelOne();
        double w2 = wInput.getWheelTwo();
        double w3 = wInput.getWheelThree();
        double w4 = wInput.getWheelFour();
        // calculate the velocity and angle rate
        double rotationRate = Kinematics.calculateVehicleRotation(wheelRadius, ROBOT_LENGTH / 2, ROBOT_HEIGHT / 2, w1, w2, w3, w4);
        double xVel = Kinematics.calculateVelocityX(wheelRadius, w1, w2, w3, w4);
        double yVel = Kinematics.calculateVelocityY(wheelRadius, w1, w2, w3, w4);
        // update the robot components
        robot.setVelocity(new Point(xVel, yVel));
        robot.setRotationRate(rotationRate);
    }

    /**
     * Based on the direction, speed, and rotation rate calculate the wheel rates and new location.
     *
     * @param input     General input
     * @param timeDelta difference between last calculations
     */
    private void calculateGeneralMovement(RobotInput input, double timeDelta) {
        GeneralInput gi = (GeneralInput) input;
        // Given the direction get the x and y component velocities
        double yVel = Math.cos(Math.toRadians(gi.getDirection() - robot.getAngle())) * gi.getSpeed();
        double xVel = Math.sin(Math.toRadians(gi.getDirection() - robot.getAngle())) * gi.getSpeed() * -1;
        robot.setVelocity(new Point(xVel, yVel));
        robot.setRotationRate(gi.getRotation());
    }

    /**
     * Calculates the robots new position and heading based on the current input and time difference.
     *
     * @param input     Current robot input
     * @param timeDelta time between frames
     */
    private void calculatePointMovement(RobotInput input, double timeDelta) {
        PointInput pi = (PointInput) input;
        if (pathIndex == pathVertices.size() - 1 && Utils.isAtGoal(robot.getLocation(), pathVertices.get(pathIndex))) {
            atGoal = true;
        } else if (Utils.isAtGoal(robot.getLocation(), pathVertices.get(pathIndex))) {
            // if we are at a vertex that is not the goal, set the local goal to the next index
            pathIndex++;
        }
        double distance = Utils.distanceBetweenPoints(robot.getLocation(), pathVertices.get(pathIndex));
        distance = distance < 0 ? distance * -1 : distance;
        if (distance <= SLOW_DOWN_DISTANCE) {
            // if we are within 1 foot of the target slow down
            double angle = Utils.getAngle(robot.getLocation(), pathVertices.get(pathIndex));
            double yVel = Math.cos(Math.toRadians(angle - robot.getAngle())) * this.speed * 0.5;
            double xVel = Math.sin(Math.toRadians(angle - robot.getAngle())) * this.speed * -1 * 0.5;
            robot.setVelocity(new Point(xVel, yVel));
            robot.setRotationRate(pi.getRotationRate());
        } else {
            double angle = Utils.getAngle(robot.getLocation(), pathVertices.get(pathIndex));
            double yVel = Math.cos(Math.toRadians(angle - robot.getAngle())) * this.speed;
            double xVel = Math.sin(Math.toRadians(angle - robot.getAngle())) * this.speed * -1;
            robot.setVelocity(new Point(xVel, yVel));
            robot.setRotationRate(pi.getRotationRate());
        }
    }

    /**
     * Calculates the robots next move based on the current vertex it is aiming for.
     *
     * @param input     Current robot input
     * @param deltaTime time between frames
     */
    private void calculateRectangleMovement(RobotInput input, double deltaTime) {
        RectanglePathInput rpi = (RectanglePathInput) input;
        if (pathIndex == pathVertices.size() - 1 && Utils.isAtGoal(robot.getLocation(), pathVertices.get(pathIndex))) {
            atGoal = true;
        } else if (Utils.isAtGoal(robot.getLocation(), pathVertices.get(pathIndex))) {
            // if we are at a vertex that is not the goal, set the local goal to the next index
            pathIndex++;
        }
        double distance = Utils.distanceBetweenPoints(robot.getLocation(), pathVertices.get(pathIndex));
        distance = distance < 0 ? distance * -1 : distance;
        if (distance <= SLOW_DOWN_DISTANCE) {
            // if we are within 1 foot of the target slow down
            double angle = Utils.getAngle(robot.getLocation(), pathVertices.get(pathIndex));
            double yVel = Math.cos(Math.toRadians(angle - robot.getAngle())) * this.speed * 0.5;
            double xVel = Math.sin(Math.toRadians(angle - robot.getAngle())) * this.speed * -1 * 0.5;
            robot.setVelocity(new Point(xVel, yVel));
            robot.setRotationRate(rpi.getRotationRate());
        } else {
            double angle = Utils.getAngle(robot.getLocation(), pathVertices.get(pathIndex));
            double yVel = Math.cos(Math.toRadians(angle - robot.getAngle())) * this.speed;
            double xVel = Math.sin(Math.toRadians(angle - robot.getAngle())) * this.speed * -1;
            robot.setVelocity(new Point(xVel, yVel));
            robot.setRotationRate(rpi.getRotationRate());
        }
    }

    /**
     * Calculates the robots next moved based on the location around the circle.
     *
     * @param input     current robot input
     * @param timeDelta time between frames
     */
    private void calculateCircleMovement(RobotInput input, double timeDelta) {
        CirclePathInput cpi = (CirclePathInput) input;
        if (pathIndex == pathVertices.size() - 1 && Utils.isAtGoal(robot.getLocation(), pathVertices.get(pathIndex))) {
            atGoal = true;
        } else if (Utils.isAtGoal(robot.getLocation(), pathVertices.get(pathIndex))) {
            pathIndex++;
        }
        double distance = Utils.distanceBetweenPoints(robot.getLocation(), pathVertices.get(pathIndex));
        distance = distance < 0 ? distance * -1 : distance;
        if (distance <= SLOW_DOWN_DISTANCE) {
            // slow down
            double angle = Utils.getAngle(robot.getLocation(), pathVertices.get(pathIndex));
            double yVel = Math.cos(Math.toRadians(angle - robot.getAngle())) * this.speed * 0.5;
            double xVel = Math.sin(Math.toRadians(angle - robot.getAngle())) * this.speed * -1 * 0.5;
            robot.setVelocity(new Point(xVel, yVel));
            robot.setRotationRate(cpi.getRotationRate());
        } else {
            double angle = Utils.getAngle(robot.getLocation(), pathVertices.get(pathIndex));
            double yVel = Math.cos(Math.toRadians(angle - robot.getAngle())) * this.speed;
            double xVel = Math.sin(Math.toRadians(angle - robot.getAngle())) * this.speed * -1;
            robot.setVelocity(new Point(xVel, yVel));
            robot.setRotationRate(cpi.getRotationRate());
        }
    }

    private void calculateFigureEightMovement(RobotInput input, double timeDelta) {
        FigureEightPathInput fepi = (FigureEightPathInput) input;
        if (pathIndex == pathVertices.size() - 1 && Utils.isAtGoal(robot.getLocation(), pathVertices.get(pathIndex))) {
            atGoal = true;
        } else if (Utils.isAtGoal(robot.getLocation(), pathVertices.get(pathIndex))) {
            pathIndex++;
        }
        double distance = Utils.distanceBetweenPoints(robot.getLocation(), pathVertices.get(pathIndex));
        distance = distance < 0 ? distance * -1 : distance;
        if (distance <= SLOW_DOWN_DISTANCE) {
            double angle = Utils.getAngle(robot.getLocation(), pathVertices.get(pathIndex));
            double yVel = Math.cos(Math.toRadians(angle - robot.getAngle())) * this.speed * 0.5;
            double xVel = Math.sin(Math.toRadians(angle - robot.getAngle())) * this.speed * -1 * 0.5;
            robot.setVelocity(new Point(xVel, yVel));
            robot.setRotationRate(fepi.getRotationRate());
        } else {
            double angle = Utils.getAngle(robot.getLocation(), pathVertices.get(pathIndex));
            double yVel = Math.cos(Math.toRadians(angle - robot.getAngle())) * this.speed;
            double xVel = Math.sin(Math.toRadians(angle - robot.getAngle())) * this.speed * -1;
            robot.setVelocity(new Point(xVel, yVel));
            robot.setRotationRate(fepi.getRotationRate());
        }
    }

    /**
     * Updates the robots current location and heading to a new one based on calculated velocity and rotation rate.
     *
     * @param xVel         GRF X-axis velocity in feet/sec
     * @param yVel         GRF Y-axis velocity in feet/sec
     * @param rotationRate Rotation rate of robot in seconds
     * @param timeDelta    Time difference between this and last frame in seconds
     * @param robot        Robot to have heading and location updated
     */
    private void updateRobot(double xVel, double yVel, double rotationRate, double timeDelta, Robot robot) {
        double angle = timeDelta * rotationRate + robot.getAngle();
        robot.setAngle(Utils.roundDouble(angle));
        // update velocities based on vehicle angle to the GRF
        Point vel = VelocityEquations.convertYawToGlobalFrame(new Position(new Point(xVel, yVel), robot.getAngle()));
        // calculate the new position data
        double newX = vel.getX() * timeDelta + robot.getLocation().getX();
        double newY = vel.getY() * timeDelta + robot.getLocation().getY();
        robot.setLocation(new Point(Utils.roundDouble(newX), Utils.roundDouble(newY)));
    }

    /**
     * Calculates the path length for the point operation.
     *
     * @param pi PointInput
     * @return distance to goal
     */
    private double calculatePointDistance(PointInput pi) {
        if (pi.getWayPoints().isEmpty()) {
            double distance = Utils.distanceBetweenPoints(robot.getLocation(), pi.getEndPoint());
            this.pathVertices.add(pi.getEndPoint());
            // make sure the distance is positive
            return distance >= 0 ? distance : -1 * distance;
        } else {
            // add in the way points
            this.pathVertices.addAll(pi.getWayPoints());
            // organize the way points
            organizeVertices(pi.getWayPoints());
            this.pathVertices.add(pi.getEndPoint());
            // calculate the total distance
            return calculateTotalPathLength();
        }
    }

    /**
     * Calculates the distance a robot needs to travel to go around a rectangle.
     *
     * @param rpi input
     * @return distance
     */
    private double calculateRectangleDistance(RectanglePathInput rpi) {
        if (rpi.getWayPoints().isEmpty()) {
            double topSides = rpi.getTopLength();
            double sides = rpi.getSideLength();
            generateRectanglePath(rpi, robot.getLocation());
            return topSides * 2 + sides * 2;
        } else {
            // update way points
//            updateWayPoints(rpi.getWayPoints(), rpi.getInclination());
            Point one = Utils.calculatePoint(robot.getLocation(), rpi.getSideLength(), rpi.getInclination());
            this.pathVertices.add(one);
            Point two = Utils.calculatePoint(one, rpi.getTopLength(), rpi.getInclination() - 90);
            this.pathVertices.add(two);
            Point three = Utils.calculatePoint(two, rpi.getSideLength(), rpi.getInclination() - 180);
            this.pathVertices.add(three);
            // organize the way points
            organizeVertices(rpi.getWayPoints());
            this.pathVertices.add(robot.getLocation());
            // calculate the total distance
            return calculateTotalPathLength();
        }
    }

    /**
     * Calculates the distance a robot need to travel to go around a circle.
     *
     * @param cpi input
     * @return distance
     */
    private double calculateCircleDistance(CirclePathInput cpi) {
        if (cpi.getWayPoints().isEmpty()) {
            generateCirclePath(cpi, robot.getLocation());
            return 2 * Math.PI * cpi.getRadius();
        } else {
            Point circleCenter = Utils.calculatePoint(robot.getLocation(), cpi.getRadius(), cpi.getInclination());
            // add vertices in 30 degree chunks
            for (int i = 30; i <= 360; i += 30) {
                this.pathVertices.add(Utils.calculatePoint(circleCenter, cpi.getRadius(), 180 + cpi.getInclination() - i));
            }
            organizeVertices(cpi.getWayPoints());
            this.pathVertices.add(robot.getLocation());
            return calculateTotalPathLength();
        }
    }

    /**
     * Calculates the distance a robot needs to travel around two circles.
     *
     * @param fepi input
     * @return distance
     */
    private double calculateFigureEightPath(FigureEightPathInput fepi) {
        if (fepi.getWayPoints().isEmpty()) {
            generateFigureEightPath(fepi, robot.getLocation());
            return (2 * Math.PI * fepi.getRadiusOne() + 2 * Math.PI * fepi.getRadiusTwo());
        } else {
            Point closeCircleCenter = Utils.calculatePoint(robot.getLocation(), fepi.getRadiusOne(), fepi.getInclination());
            Point farCircleCenter = Utils.calculatePoint(robot.getLocation(), fepi.getRadiusOne() * 2 + fepi.getRadiusTwo(), fepi.getInclination());
            // go through the first half
            for (int i = 30; i <= 180; i += 30) {
                this.pathVertices.add(Utils.calculatePoint(closeCircleCenter, fepi.getRadiusOne(), 180 + fepi.getInclination() - i));
            }
            // add the far circle
            for (int i = 360; i >= 0; i -= 30) {
                this.pathVertices.add(Utils.calculatePoint(farCircleCenter, fepi.getRadiusTwo(), 180 + fepi.getInclination() - i));
            }
            // add the rest of the last circle
            for (int i = 180 + 30; i < 360; i += 30) {
                this.pathVertices.add(Utils.calculatePoint(closeCircleCenter, fepi.getRadiusOne(), 180 + fepi.getInclination() - i));
            }
            organizeVertices(fepi.getWayPoints());
            this.pathVertices.add(robot.getLocation());
            return calculateTotalPathLength();
        }
    }

    /**
     * Verifies the robot doesn't have to speed to complete the path.
     *
     * @param time     time to complete path
     * @param distance distance to travel for path
     * @return flag indicating if the path at that time is possible
     */
    private boolean verifyPathCompletion(double time, double distance) {
        if (time <= 0) {
            return true;
        }
        // make sure the robot doesn't speed
        if (distance / time <= 15.0) {
            return true;
        }
        return false;
    }

    /**
     * Calculates the four points of a rectangle in clockwise order and adds them to an ArrayList.
     *
     * @param rpi              input for getting distance between points
     * @param startingLocation input for getting the location of the first point
     */
    private void generateRectanglePath(RectanglePathInput rpi, Point startingLocation) {
        Point one = Utils.calculatePoint(startingLocation, rpi.getSideLength(), rpi.getInclination());
        this.pathVertices.add(one);
        Point two = Utils.calculatePoint(one, rpi.getTopLength(), rpi.getInclination() - 90);
        this.pathVertices.add(two);
        Point three = Utils.calculatePoint(two, rpi.getSideLength(), rpi.getInclination() - 180);
        this.pathVertices.add(three);
        this.pathVertices.add(startingLocation);
    }

    /**
     * Calculates two points, one at the "top" of the circle and the starting point.
     * Robot will move to the "top" point then back to the starting point.
     *
     * @param cpi           input for circle path
     * @param startingPoint starting location
     */
    private void generateCirclePath(CirclePathInput cpi, Point startingPoint) {
        Point circleCenter = Utils.calculatePoint(startingPoint, cpi.getRadius(), cpi.getInclination());
        // add vertices in 30 degree chunks
        for (int i = 30; i <= 360; i += 30) {
            this.pathVertices.add(Utils.calculatePoint(circleCenter, cpi.getRadius(), 180 + cpi.getInclination() - i));
        }
        this.pathVertices.add(startingPoint);
    }

    /**
     * Start with the first half of one circle, transfer to the entire second circle, then finish the first.
     *
     * @param fepi          input for figure eight
     * @param startingPoint starting point
     */
    private void generateFigureEightPath(FigureEightPathInput fepi, Point startingPoint) {
        Point closeCircleCenter = Utils.calculatePoint(startingPoint, fepi.getRadiusOne(), fepi.getInclination());
        Point farCircleCenter = Utils.calculatePoint(startingPoint, fepi.getRadiusOne() * 2 + fepi.getRadiusTwo(), fepi.getInclination());
        // go through the first half
        for (int i = 30; i <= 180; i += 30) {
            this.pathVertices.add(Utils.calculatePoint(closeCircleCenter, fepi.getRadiusOne(), 180 + fepi.getInclination() - i));
        }
        // add the far circle
        for (int i = 360; i >= 0; i -= 30) {
            this.pathVertices.add(Utils.calculatePoint(farCircleCenter, fepi.getRadiusTwo(), 180 + fepi.getInclination() - i));
        }
        // add the rest of the last circle
        for (int i = 180 + 30; i < 360; i += 30) {
            this.pathVertices.add(Utils.calculatePoint(closeCircleCenter, fepi.getRadiusOne(), 180 + fepi.getInclination() - i));
        }
        this.pathVertices.add(startingPoint);
    }

    /**
     * Organize the vertices based on the distance from one another.
     *
     * @param wayPoints way points to add to the path
     */
    private void organizeVertices(ArrayList<Point> wayPoints) {
        for (int i = 0; i < wayPoints.size(); i++) {
            double distance = Utils.distanceBetweenPoints(wayPoints.get(i), pathVertices.get(0));
            distance = distance < 0 ? distance * -1 : distance;
            int index = 0;
            for (int j = 1; j < pathVertices.size(); j++) {
                double temp = Utils.distanceBetweenPoints(wayPoints.get(i), pathVertices.get(j));
                temp = temp < 0 ? temp * -1 : temp;
                if (temp < distance) {
                    index = j;
                    distance = temp;
                }
            }
            pathVertices.add(index + 1, wayPoints.get(i));
        }
    }

    /**
     * Calculates the total path length with or without the way points.
     *
     * @return distance
     */
    private double calculateTotalPathLength() {
        double distance = 0.0;
        double temp;
        Point previousPoint = robot.getLocation();
        for (int i = 0; i < pathVertices.size(); i++) {
            temp = Utils.distanceBetweenPoints(previousPoint, pathVertices.get(i));
            distance += temp >= 0 ? temp : -1 * temp;
            previousPoint = pathVertices.get(i);
        }
        return distance;
    }
}
