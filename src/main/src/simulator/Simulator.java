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
    private RobotInput input;

    public Simulator(RobotInput input, Robot robot) {
        this.input = input;
        this.robot = robot;
        initalizeRobot(input, this.robot);
    }

    /**
     * Due to different input classes having different features make sure input variables are added.
     *
     * @param input Input of the robot
     * @param robot Robot to update
     */
    private void initalizeRobot(RobotInput input, Robot robot) {
        switch (input.getMode()) {
            case CONTROL_GENERAL:
                robot.setRotationRate(((GeneralInput) input).getRotation());
                break;
            case CONTROL_WHEELS: // Not needed all four wheel rates are read later
                break;
            default:
                System.err.println("Not implemented");
        }
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
     * Given the defined input, calculate the new position of the robot.
     *
     * @param timeDelta Time difference between this calculation and the last one
     */
    public void calculateNewPosition(double timeDelta) {
        boolean recalculateFlag = false;
        if (lastRecalculation > RECALCULATE_COURSE) {
            recalculateFlag = true;
            lastRecalculation = 0.0;
        }
        lastRecalculation += timeDelta;
//        System.out.println("Last recalc: " + lastRecalculation);
        // calculate the course
        switch (input.getMode()) {
            case CONTROL_WHEELS:
                calculateWheelMovement(input, timeDelta);
                break;
            case CONTROL_GENERAL:
                if (recalculateFlag) {
                    recalculateGeneralCourse(input, timeDelta);
                    recalculateFlag = false;
                }
                calculateGeneralMovement(input, timeDelta);
                break;
            case POINT:
                if (recalculateFlag) {
                    //recalculatePointCourse(input, timeDelta);
                    recalculateFlag = false;
                }
                calculatePointMovement(input, timeDelta);
                break;
            default:
                System.err.println("Not implemented");
        }
    }

    private void recalculateGeneralCourse(RobotInput input, double timeDelta) {
        // find out how far off course the robot is
        GeneralInput gi = (GeneralInput) input;
        double robotY = robot.getLocation().getY();
        double robotX = robot.getLocation().getX();
        double startPointX = gi.getStartLocation().getX();
        double startPointY = gi.getStartLocation().getY();
        double endPointY = robotY;
        double endPointX = robotY / Math.tan(Math.toRadians(gi.getDirection()));
        double yTwoMinusYOne = endPointY - startPointY;
        double xTwoMinusXOne = endPointX - startPointX;
        double nominator = Math.abs(yTwoMinusYOne * robotX - xTwoMinusXOne * robotY + endPointX * startPointY - endPointY * startPointX);
        double denominator = Math.sqrt(Math.pow((endPointY - startPointY), 2) + Math.pow((endPointX - startPointX), 2));
        double distance = nominator / denominator;
//        System.out.println("Distance from line: " + distance);
    }

    /**
     * Based on the direction, speed, and rotation rate calculate the wheel rates and new location.
     *
     * @param input     General input
     * @param timeDelta difference between last calculations
     */
    private void calculateGeneralMovement(RobotInput input, double timeDelta) {
        GeneralInput gInput = (GeneralInput) input;
        // TODO: based on the current location calculate if a course correction is required
        // Given the direction get the x and y component velocities
        double yVel = Math.cos(Math.toRadians(gInput.getDirection())) * gInput.getSpeed();
        double xVel = Math.sin(Math.toRadians(gInput.getDirection())) * gInput.getSpeed();
        // get the new robot angle
        double newAngle = gInput.getRotation() * timeDelta + robot.getAngle();
        // update the robot
        updateRobot(xVel, yVel, newAngle, robot.getRotationRate(), timeDelta, robot);
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
        double rotationRate = Kinematics.calculateVehicleRotation(WHEEL_RADIUS, ROBOT_LENGTH, ROBOT_HEIGHT, w1, w2, w3, w4);
        double xVel = Kinematics.calculateVelocityX(WHEEL_RADIUS, w1, w2, w3, w4);
        double yVel = Kinematics.calculateVelocityY(WHEEL_RADIUS, w1, w2, w3, w4);

        // calculate the new angle based on rate and time difference between last calculation
        double newAngle = rotationRate * timeDelta + robot.getAngle();
        // update the robot
        updateRobot(xVel, yVel, newAngle, rotationRate, timeDelta, robot);
    }

    private void calculatePointMovement(RobotInput input, double timeDelta) {
        PointInput pi = (PointInput) input;
        double yVel = Math.cos(Math.toRadians(robot.getAngle())) * pi.getSpeed();
        double xVel = Math.sin(Math.toRadians(robot.getAngle())) * pi.getSpeed();
        // get the new robot angle
        double newAngle = pi.getRotationRate() * timeDelta + robot.getAngle();
        // update the robot position
        updateRobot(xVel, yVel, newAngle, pi.getRotationRate(), timeDelta, robot);
    }

    /**
     * Updates the robots current location to a new one based on velocity and heading.
     *
     * @param xVel         GRF X-axis velocity
     * @param yVel         GRF Y-axis velocity
     * @param newAngle     GRF angle of robot
     * @param rotationRate Rotation rate of robot
     * @param timeDelta    Time difference between this and last frame
     * @param robot        Robot
     */
    private void updateRobot(double xVel, double yVel, double newAngle, double rotationRate, double timeDelta, Robot robot) {
        robot.setVelocity(new Point(xVel, yVel));
        robot.setRotationRate(rotationRate);
        robot.setAngle(newAngle);
        // update velocities based on vehicle angle
        robot.setVelocity(VelocityEquations.convertYawToGlobalFrame(new Position(robot.getVelocity(), robot.getAngle())));
        // calculate the new position data
        double newX = robot.getVelocity().getX() * timeDelta + robot.getLocation().getX();
        double newY = robot.getVelocity().getY() * timeDelta + robot.getLocation().getY();
        robot.setLocation(new Point(newX, newY));
    }
}
