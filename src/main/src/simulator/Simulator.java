package simulator;

import robot.Kinematics;
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
 *
 * Created by CyberPuck on 2016-02-26.
 */
public class Simulator {
    // TODO: Should this be hard coded?
    private double WHEEL_RADIUS = 0.5;
    private double ROBOT_LENGTH = 2;
    private double ROBOT_HEIGHT = 4;
    // handle on the data to move the robot
    private RobotInput input;

    public Simulator(RobotInput input) {
        this.input = input;
    }

    /**
     * Given the defined input, calculate the new position of the robot.
     * @param currentPosition Current position and orientation of the robot
     * @return New position of the robot
     */
    public Position calculateNewPosition(Position currentPosition) {
        Position pos;
        switch(input.getMode()) {
            case CONTROL_WHEELS:
                pos = calculateWheelPosition(input, currentPosition);
                break;
            default:
                System.err.println("Not implemented");
                pos = currentPosition;
        }
        return pos;
    }

    private Position calculateWheelPosition(RobotInput input, Position currentPosition) {
        WheelInput wInput = (WheelInput)input;
        // Get the wheel rates
        double w1 = wInput.getWheelOne();
        double w2 = wInput.getWheelTwo();
        double w3 = wInput.getWheelThree();
        double w4 = wInput.getWheelFour();
        // calculate the velocity and angle rate
        double angle = Kinematics.calculateVehicleRotation(WHEEL_RADIUS, ROBOT_LENGTH, ROBOT_HEIGHT, w1, w2, w3, w4);
        double x = Kinematics.calculateVelocityX(WHEEL_RADIUS, w1, w2, w3, w4);
        double y = Kinematics.calculateVelocityY(WHEEL_RADIUS, w1, w2, w3, w4);
        // apply velocity and angle rates
        double newX = currentPosition.getPosition().getX() + x;
        double newY = currentPosition.getPosition().getY() + y;
        double newAngle = currentPosition.getAngle() + angle;
        return new Position(new Point(newX, newY), newAngle);
    }
}
