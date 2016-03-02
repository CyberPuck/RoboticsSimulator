package UI;

import inputs.RobotInput;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import robot.Robot;
import simulator.Simulator;
import utilities.Point;
import utilities.Position;

import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ResourceBundle;

/**
 * Primary controller for the entire UI.
 * <p/>
 * Created by CyberPuck on 2016-02-15.
 */
public class SimulatorController implements Initializable {
    // TODO: Should this be configurable? in feet
    private static double WHEEL_RADIUS = 0.25;

    @FXML
    private Pane displayPane;

    @FXML
    private TabPane controlPane;

    // System state variables
    @FXML
    private Pane systemStatePane;
    @FXML
    private Label velocityY;
    @FXML
    private Label velocityX;
    @FXML
    private Label direction;
    @FXML
    private Label position;
    @FXML
    private Label wheels;
    @FXML
    private Label rotationRate;

    // TODO: Should we move these?
    @FXML
    private Canvas gridCanvas;
    @FXML
    private Canvas robotCanvas;
    @FXML
    private Canvas pathCanvas;
    @FXML
    private TextArea outputTextArea;

    private long previousTime = 0;
    // controller for the simulation display
    private DisplayPaneController displayController;
    private AnimationTimer timer;
    // Location of the robot in the global reference frame
    private Position robotPosition;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        Background test = new Background(new BackgroundFill(Color.BURLYWOOD, CornerRadii.EMPTY, Insets.EMPTY));
        //displayPane.setBackground(test);
//        systemStatePane.setBackground(test);
        // add in the individual controllers
        displayController = new DisplayPaneController(displayPane, gridCanvas, robotCanvas, pathCanvas);
        displayController.initializePane();
        // add in the tab controller
        TabController tabControl = new TabController(controlPane, this);
        tabControl.init();
        // force the text area to auto scroll
        outputTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                outputTextArea.setScrollTop(Double.MAX_VALUE);
            }
        });
        // robot always starts in the middle
        robotPosition = new Position(new Point(7.5, 15), 0.0);
    }

    /**
     * TODO: Should we return error values?
     */
    public void startSimulator(RobotInput input) {
        // clear the robot path
        displayController.getPathCanvas().restartCanvas();
        // first get the current robot position and orientation
        Position startPos = displayController.getRobotCanvas().convertLocationToFeet();
        final Robot robot = new Robot(WHEEL_RADIUS);
        robot.setLocation(robotPosition.getPosition());
        robot.setAngle(robotPosition.getAngle());
        robot.setVelocity(new Point(0, 0));
        // update the robot stats
        updateSystemState(robot);
        // start the simulator
        printText("Starting simulation");
        System.out.println("Starting Simulator");
        final Simulator sim = new Simulator(input, robot);
        previousTime = 0;
//        final long previousTime = 0;
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Calculate the delta between this frame and the last
                double deltaTime = 0;
                if (previousTime != 0) {
                    deltaTime = now - previousTime;
                    // convert to seconds
                    deltaTime = deltaTime / 1000000000.0;
                }
                previousTime = now;
                // Debug code, need to add logic to only update every x frames
//                printText("Now: " + now);
                // TODO: Need to convert the Y position into the canvas coordinates
                // update the robot position
                sim.calculateNewPosition(deltaTime);
                // update the reference position
                robotPosition = new Position(sim.getRobot().getLocation(), robot.getAngle());
                // update the robot data
                updateSystemState(sim.getRobot());
                // update the position based on the global reference frame
//                displayController.getRobotCanvas().setRobotPosition(sim.getRobot());
                displayController.getRobotCanvas().redrawRobot(sim.getRobot());
            }
        };
        timer.start();
    }

    /**
     * TODO: Should we return error values?
     */
    public void stopSimulator() {
        printText("Stopping Simulator");
        // zero out velocities on the system display
        stopSystemState();
        // Kill the timer
        if (timer != null) {
            timer.stop();
            // clean out this bad boy
            timer = null;
        }
    }

    /**
     * Prints out text on the output pane.
     *
     * @param text string to print
     */
    public void printText(String text) {
        outputTextArea.setText(outputTextArea.getText() + text + "\n");
        outputTextArea.appendText("");
    }

    private void updateSystemState(Robot robot) {
        // update the velocity components
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.HALF_UP);
        velocityY.setText("Velocity Y: " + df.format(robot.getVelocity().getY()));
        velocityX.setText("Velocity X: " + df.format(robot.getVelocity().getX()));
        rotationRate.setText("Rotation Rate: " + df.format(robot.getRotationRate()));
        direction.setText("Direction: " + df.format(robot.getAngle()));
        position.setText("Position: (" + df.format(robot.getLocation().getX()) + ", "
                + df.format(robot.getLocation().getY()) + ")");
        double[] wheelRates = robot.getWheelRates();
        wheels.setText("Wheel One: " + df.format(wheelRates[0]) + ", Wheel Two: "
                + df.format(wheelRates[1]) + ", Wheel Three: " + df.format(wheelRates[2])
                + ", Wheel Four: " + df.format(wheelRates[3]));
    }

    private void stopSystemState() {
        velocityY.setText("Velocity Y: 0.0");
        velocityX.setText("Velocity X: 0.0");
        rotationRate.setText("Rotation Rate: 0.0");
        wheels.setText("Wheel One: " + 0.0 + ", Wheel Two: "
                + 0.0 + ", Wheel Three: " + 0.0
                + ", Wheel Four: " + 0.0);
    }

    public Position getRobotPosition() {
        return robotPosition;
    }
}
