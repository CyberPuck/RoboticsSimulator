package simulator;

import inputs.GeneralInput;
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
    private double WHEEL_RADIUS = 0.5;
    private double ROBOT_LENGTH = 2;
    private double ROBOT_HEIGHT = 4;
    // handle on the data to move the robot
    private Robot robot;
    private RobotInput input;

    public Simulator(RobotInput input, Robot robot) {
        this.input = input;
        this.robot = robot;
    }

    public void setRobot(Robot robot) {
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
        switch (input.getMode()) {
            case CONTROL_WHEELS:
                calculateWheelMovement(input, timeDelta);
                break;
            case CONTROL_GENERAL:
                calculateGeneralMovement(input, timeDelta);
                break;
            default:
                System.err.println("Not implemented");
        }
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
