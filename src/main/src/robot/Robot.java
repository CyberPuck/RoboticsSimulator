package robot;

import utilities.Point;

/**
 * This class contains the position, rotation, and velocity (X and Y) of the robot.
 * It will need to have it's coordinates translated by the robotCanvas.
 * <p/>
 * Created by CyberPuck on 2016-02-28.
 */
public class Robot {
    private Point location;
    // velocity is held as a point since it has an X and Y component, in feet
    private Point velocity;
    // rotation rate of the vehicle, angle per second
    private double rotationRate;
    // current rotation of the robot relative to the global frame
    private double angle;
    // radius of the wheels, in feet
    private double radius;
    // height is the x-axis component between wheels and center in feet
    private static double HEIGHT = 1.0;
    // length is the y-axis component between axel and center in feet
    private static double LENGTH = 2.0;

    public Robot(double radius) {
        location = new Point(0, 0);
        velocity = new Point(0, 0);
        rotationRate = 0;
        angle = 0;
        this.radius = radius;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public Point getVelocity() {
        return velocity;
    }

    public void setVelocity(Point velocity) {
        this.velocity = velocity;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    /**
     * Converts the velocity to the current wheel rates of the vehicle.
     *
     * @return Array with four doubles, index refers to wheel on robot
     */
    public double[] getWheelRates() {
        double wheels[] = {0, 0, 0, 0};
        wheels[0] = Kinematics.calculateWheelOneRotation(radius, HEIGHT, LENGTH, velocity.getY(), velocity.getX(), rotationRate);
        wheels[1] = Kinematics.calculateWheelTwoRotation(radius, HEIGHT, LENGTH, velocity.getY(), velocity.getX(), rotationRate);
        wheels[2] = Kinematics.calculateWheelThreeRotation(radius, HEIGHT, LENGTH, velocity.getY(), velocity.getX(), rotationRate);
        wheels[3] = Kinematics.calculateWheelFourRotation(radius, HEIGHT, LENGTH, velocity.getY(), velocity.getX(), rotationRate);
        return wheels;
    }

    @Override
    public String toString() {
        return "Robot{" +
                "location=" + location +
                ", velocity=" + velocity +
                ", angle=" + angle +
                ", radius=" + radius +
                '}';
    }
}
