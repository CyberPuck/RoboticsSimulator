package simulator;

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
                calculateWheelPosition(input, timeDelta);
                break;
            default:
                System.err.println("Not implemented");
        }
    }

    /**
     * Based on user input and the time difference calculate the new location, Vs, and angle.
     *
     * @param input     User input for wheel rotations
     * @param timeDelta Difference between last calculation
     */
    private void calculateWheelPosition(RobotInput input, double timeDelta) {
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
        robot.setVelocity(new Point(xVel, yVel));
        robot.setRotationRate(rotationRate);
        // calculate the new angle based on rate and time difference between last calculation
        double newAngle = rotationRate * timeDelta + robot.getAngle();
        robot.setAngle(newAngle);
        // update velocities based on vehicle angle
        robot.setVelocity(VelocityEquations.convertYawToGlobalFrame(new Position(robot.getVelocity(), robot.getAngle())));
        // calculate the new position data
        double newX = robot.getVelocity().getX() * timeDelta + robot.getLocation().getX();
        double newY = robot.getVelocity().getY() * timeDelta + robot.getLocation().getY();
        robot.setLocation(new Point(newX, newY));
    }
}
