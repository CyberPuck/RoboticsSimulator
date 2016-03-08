package robot;

/**
 * Kinematic equations for a robot.
 * <p/>
 * Created by CyberPuck on 2016-02-27.
 */
public class Kinematics {

    /**
     * Given four wheel rotation rates calculate the Y velocity of the robot.
     *
     * @param radius    Radius of the wheels
     * @param rateOne   Upper left wheel rate
     * @param rateTwo   Upper right wheel rate
     * @param rateThree Lower left wheel rate
     * @param rateFour  Lower right wheel rate
     * @return VelocityY
     */
    public static double calculateVelocityY(double radius, double rateOne, double rateTwo, double rateThree, double rateFour) {
        double vY = (radius / 4) * (rateOne + rateTwo + rateThree + rateFour);
        return vY;
    }

    /**
     * Given four wheel rotation rates calculate the X velocity of the robot.
     *
     * @param radius    Radius of the wheels
     * @param rateOne   Upper left wheel rate
     * @param rateTwo   Upper right wheel rate
     * @param rateThree Lower left wheel rate
     * @param rateFour  Lower right wheel rate
     * @return VelocityX
     */
    public static double calculateVelocityX(double radius, double rateOne, double rateTwo, double rateThree, double rateFour) {
        double vX = (radius / 4) * (rateOne - rateTwo - rateThree + rateFour);
        return vX;
    }

    /**
     * Given four wheel rotation rates calculate the z-axis rotation of the robot.
     *
     * @param radius    Radius of the wheels
     * @param width     x-axis distance between wheel and center
     * @param length    x-axis distance between wheel and center
     * @param rateOne   Upper left wheel rate
     * @param rateTwo   Upper right wheel rate
     * @param rateThree Lower left wheel rate
     * @param rateFour  Lower right wheel rate
     * @return vehicle rotation rate
     */
    public static double calculateVehicleRotation(double radius, double width, double length, double rateOne, double rateTwo, double rateThree, double rateFour) {
        double rotationRate = radius / (4 * (width + length));
        rotationRate *= (-1 * rateOne + rateTwo - rateThree + rateFour);
        return rotationRate;
    }

    /**
     * Given the velocities, rotation rate, and vehicle attributes, calculate the upper left wheel rotation.
     *
     * @param radius   radius of the wheels
     * @param width    x-axis distance between wheel and center
     * @param length   x-axis distance between wheel and center
     * @param vY       y-axis velocity of robot
     * @param vX       x-axis velocity of robot
     * @param rotation rotation rate of robot
     * @return rotation rate of wheel one (upper left)
     */
    public static double calculateWheelOneRotation(double radius, double width, double length, double vY, double vX, double rotation) {
        double wheelOne = (1 / radius) * (vX + vY - rotation * (width + length));
        return wheelOne;
    }

    /**
     * Given the velocities, rotation rate, and vehicle attributes, calculate the upper right wheel rotation.
     *
     * @param radius   radius of the wheels
     * @param width    x-axis distance between wheel and center
     * @param length   x-axis distance between wheel and center
     * @param vY       y-axis velocity of robot
     * @param vX       x-axis velocity of robot
     * @param rotation rotation rate of robot
     * @return rotation rate of wheel two (upper right)
     */
    public static double calculateWheelTwoRotation(double radius, double width, double length, double vY, double vX, double rotation) {
        double wheelTwo = (1 / radius) * (-vX + vY + rotation * (width + length));
        return wheelTwo;
    }

    /**
     * Given the velocities, rotation rate, and vehicle attributes, calculate the lower left wheel rotation.
     *
     * @param radius   radius of the wheels
     * @param width    x-axis distance between wheel and center
     * @param length   x-axis distance between wheel and center
     * @param vY       y-axis velocity of robot
     * @param vX       x-axis velocity of robot
     * @param rotation rotation rate of robot
     * @return rotation rate of wheel three (lower left)
     */
    public static double calculateWheelThreeRotation(double radius, double width, double length, double vY, double vX, double rotation) {
        double wheelThree = (1 / radius) * (-vX + vY - rotation * (width + length));
        return wheelThree;
    }

    /**
     * Given the velocities, rotation rate, and vehicle attributes, calculate the lower right wheel rotation.
     *
     * @param radius   radius of the wheels
     * @param width    x-axis distance between wheel and center
     * @param length   x-axis distance between wheel and center
     * @param vY       y-axis velocity of robot
     * @param vX       x-axis velocity of robot
     * @param rotation rotation rate of robot
     * @return rotation rate of wheel four (lower right)
     */
    public static double calculateWheelFourRotation(double radius, double width, double length, double vY, double vX, double rotation) {
        double wheelFour = (1 / radius) * (vX + vY + rotation * (width + length));
        return wheelFour;
    }
}
