package simulator;

import inputs.GeneralInput;
import inputs.PointInput;
import inputs.RobotInput;
import inputs.WheelInput;
import robot.Kinematics;
import robot.Robot;
import robot.VelocityEquations;
import utilities.Point;
import utilities.Position;
import utilities.Utils;

/**
 * This is the simulator class, like highlander there can be only one.
 * Handles: path planning, movement, and conversion to canvas coordinates.
 * TODO:
 * Add kinematic equations
 * Add draw loop
 * Determine wheel radius
 * Determine recalculation rate
 * <p/>
 * Created by CyberPuck on 2016-02-26.
 */
public class Simulator {
    // TODO: Should this be hard coded?
    private static double WHEEL_RADIUS = 0.5;
    private static double ROBOT_LENGTH = 2;
    private static double ROBOT_HEIGHT = 4;
    // every 100 ms recalculate the course to take
    private static double RECALCULATE_COURSE = 0.1;
    private double lastRecalculation;
    // handle on the data to move the robot
    private Robot robot;
    // current input for the robot to move
    private RobotInput input;
    // original input, provided by the UI
    private RobotInput originalInput;
    // flag indicating if the goal has been reached
    private boolean atGoal;
    // flag indicating if the simulation can complete in time
    private boolean enoughTime;

    public Simulator(RobotInput input, Robot robot) {
        this.input = input;
        this.originalInput = input;
        this.robot = robot;
        enoughTime = initializeRobot(input, this.robot);
        atGoal = false;
    }

    /**
     * Due to different input classes having different features make sure input variables are added.
     *
     * @param input Input of the robot
     * @param robot Robot to update
     */
    private boolean initializeRobot(RobotInput input, Robot robot) {
        switch (input.getMode()) {
            case CONTROL_GENERAL:
                robot.setRotationRate(((GeneralInput) input).getRotation());
                break;
            case POINT:
                PointInput pi = (PointInput) input;
                double distance = calculatePointDistance(pi);
                if (!verifyPointCompletion(pi.getTime(), distance)) {
                    return false;
                }
                robot.setRotationRate(pi.getRotationRate());
                ((PointInput) input).setSpeed(distance / pi.getTime());
                break;
            case CONTROL_WHEELS: // Not needed all four wheel rates are read later
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
     * Given the defined input, calculate the new position of the robot.
     *
     * @param timeDelta Time difference between this calculation and the last one
     */
    public void calculateNewPosition(double timeDelta) {
        lastRecalculation += timeDelta;
//        System.out.println("Last recalc: " + lastRecalculation);
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
        double rotationRate = Kinematics.calculateVehicleRotation(WHEEL_RADIUS, ROBOT_LENGTH / 2, ROBOT_HEIGHT / 2, w1, w2, w3, w4);
        double xVel = Kinematics.calculateVelocityX(WHEEL_RADIUS, w1, w2, w3, w4);
        double yVel = Kinematics.calculateVelocityY(WHEEL_RADIUS, w1, w2, w3, w4);
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
        // TODO: based on the current location calculate if a course correction is required
        // Given the direction get the x and y component velocities
        double yVel = Math.cos(Math.toRadians(gi.getDirection() - robot.getAngle())) * gi.getSpeed();
        double xVel = Math.sin(Math.toRadians(gi.getDirection() - robot.getAngle())) * gi.getSpeed() * -1;
        robot.setVelocity(new Point(xVel, yVel));
        robot.setRotationRate(gi.getRotation());
        System.out.println("Current angle: " + Math.toDegrees(Math.atan(yVel / xVel)));
    }

    /**
     * Calculates the robots new position and heading based on the current input and time difference.
     *
     * @param input     Current robot input
     * @param timeDelta time between frames
     */
    private void calculatePointMovement(RobotInput input, double timeDelta) {
        PointInput pi = (PointInput) input;
        // Check if we made it to the goal
        if (Utils.isAtGoal(robot.getLocation(), pi.getEndPoint())) {
            atGoal = true;
        }
        double angle = Utils.getAngle(robot.getLocation(), pi.getEndPoint());
        System.out.println("Angle to point: " + angle);
        double yVel = Math.cos(Math.toRadians(angle - robot.getAngle())) * pi.getSpeed();
        double xVel = Math.sin(Math.toRadians(angle - robot.getAngle())) * pi.getSpeed() * -1;
        System.out.println("New velocities: " + xVel + ", " + yVel);
        robot.setVelocity(new Point(xVel, yVel));
        robot.setRotationRate(pi.getRotationRate());
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
        System.out.println("angle of robot: " + angle);
        robot.setAngle(Utils.roundDouble(angle));
        // update velocities based on vehicle angle to the GRF
        Point vel = VelocityEquations.convertYawToGlobalFrame(new Position(new Point(xVel, yVel), robot.getAngle()));
        System.out.println("Converted velocity: " + vel.toString());
        // calculate the new position data
        double newX = vel.getX() * timeDelta + robot.getLocation().getX();
        double newY = vel.getY() * timeDelta + robot.getLocation().getY();
        robot.setLocation(new Point(Utils.roundDouble(newX), Utils.roundDouble(newY)));
//        System.out.println(robot.toString());
    }

    /**
     * Calculates the path length for the point operation.
     *
     * @param pi PointInput
     * @return distance to goal
     */
    private double calculatePointDistance(PointInput pi) {
        double distance = Utils.distanceBetweenPoints(robot.getLocation(), pi.getEndPoint());
        // make sure the distance is positive
        return distance >= 0 ? distance : -1 * distance;
    }

    /**
     * Verifies the robot doesn't have to speed to complete the path.
     *
     * @param time     time to complete path
     * @param distance distance to travel for path
     * @return flag indicating if the path at that time is possible
     */
    private boolean verifyPointCompletion(double time, double distance) {
        if (time <= 0) {
            return true;
        }
        // make sure the robot doesn't speed
        if (distance / time <= 15.0) {
            return true;
        }
        return false;
    }
}
