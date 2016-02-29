package robot;

import utilities.Point;
import utilities.Position;

/**
 * Class to handle converting raw velocity and angle and calculate the correct rotation
 * and direction.
 * <p/>
 * Created by CyberPuck on 2016-02-28.
 */
public class VelocityEquations {

    /**
     * Given the position and angle relative to the global frame, calculate the new position.
     * Based on the Yaw (Z-axis) rotation of the robot.
     *
     * @param robotPos Current position based on robot reference frame
     * @return Current position based on the inertial reference frame
     */
    public static Point convertYawToGlobalFrame(Position robotPos) {
        Point pos = new Point(robotPos.getPosition().getX(), robotPos.getPosition().getY());
        // convert angle to radians
        double gamma = Math.toRadians(robotPos.getAngle());
        double robotX = robotPos.getPosition().getX();
        double robotY = robotPos.getPosition().getY();
        pos.setX((Math.cos(gamma) * robotX) - (Math.sin(gamma) * robotY));
        pos.setY((Math.sin(gamma) * robotX) + (Math.cos(gamma) * robotY));
        return pos;
    }
}
