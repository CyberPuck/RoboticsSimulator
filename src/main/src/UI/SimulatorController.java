package UI;

import inputs.InputMode;
import inputs.RobotInput;
import inputs.WheelInput;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import robot.Robot;
import simulator.Simulator;
import utilities.Point;
import utilities.Position;
import utilities.Utils;

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

    // parent node, need this for detecting mouse and keyboard actions
    @FXML
    private Pane mainPane;
    // displays the robot
    @FXML
    private Pane displayPane;
    // UI pane
    @FXML
    private TabPane controlPane;
    // System state variables
//    @FXML
//    private Pane systemStatePane;
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
    // Reset the veiw
    @FXML
    private Button resetButton;

    // Canvas to draw the robot
    // TODO: If time move these to separate controllers.
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
    // controller for the tabs
    private TabController tabController;
    // animation timer to draw a moving robot and path
    private AnimationTimer timer;
    // Location of the robot in the global reference frame
    private Position robotPosition;
    // flag indicating if the simulator is running
    private boolean simulatorRunning = false;
    // input from the UI, if any
    private RobotInput input;
    // used for the timer, display this at the end of every simulation
    private long startTime;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // robot always starts in the middle
        robotPosition = new Position(new Point(0, 0), 0.0);
        // add in the individual controllers
        displayController = new DisplayPaneController(displayPane, gridCanvas, robotCanvas, pathCanvas);
        // Inform all canvases of the center
        displayController.initializePane(robotPosition.getPosition());
        // add in the tab controller
        tabController = new TabController(controlPane, this);
        tabController.init();
        // force the text area to auto scroll
        outputTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                outputTextArea.setScrollTop(Double.MAX_VALUE);
            }
        });
        Robot robot = new Robot(WHEEL_RADIUS);
        robot.setLocation(robotPosition.getPosition());
        // update the system status
        updateSystemState(robot);
        // setup the reset button
        resetButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // make sure to stop the simulation
                if (simulatorRunning) {
                    stopSimulator();
                }
                // move the robot
                displayController.getPathCanvas().restartCanvas();
                // update the robot to origin
                robotPosition = new Position(new Point(0, 0), 0.0);
                displayController.initializePane(new Point(0, 0));
                // restart system state
                resetSystemState();
                // update the tabs to reflect new state
                tabController.updateUIs();
                printText("Resetting Simulation environment");
            }
        });

        // setup the main pane to handle input
        mainPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (simulatorRunning && (input.getMode() == InputMode.CONTROL_WHEELS || input.getMode() == InputMode.CONTROL_GENERAL)) {
                    stopSimulator();
                }
            }
        });

        mainPane.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (simulatorRunning && (input.getMode() == InputMode.CONTROL_WHEELS || input.getMode() == InputMode.CONTROL_GENERAL)) {
                    stopSimulator();
                }
            }
        });
    }

    /**
     * TODO: Should we return error values?
     */
    public void startSimulator(final RobotInput input) {
        startTime = System.currentTimeMillis();
        this.input = input;
        simulatorRunning = true;
        // setup the robot object
        final Robot robot = new Robot(WHEEL_RADIUS);
        robot.setLocation(robotPosition.getPosition());
        robot.setAngle(robotPosition.getAngle());
        robot.setVelocity(new Point(0, 0));
        // clear the robot path
        displayController.getPathCanvas().restartCanvas();
        // draw the input path
        displayController.getPathCanvas().setInput(input);
        displayController.getPathCanvas().setStartingLocation(Utils.convertLocationToPixels(robot.getLocation()));
        displayController.getPathCanvas().redrawInputPath();
        // update the robot stats
        updateSystemState(robot);
        // check if the wheel rotation is static
        if (input.getMode() == InputMode.CONTROL_WHEELS) {
            updateWheeledState((WheelInput) input);
        }
        // start the simulator
        System.out.println("Starting Simulator");
        final Simulator sim = new Simulator(input, robot);
        previousTime = 0;
//        final long previousTime = 0;
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // make sure to only update when the simulator is running
                if (simulatorRunning) {
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
                    // check if the wheel rotation is static
                    if (input.getMode() == InputMode.CONTROL_WHEELS) {
                        updateWheeledState((WheelInput) input);
                    }
                    // update the position based on the global reference frame
//                displayController.getRobotCanvas().setRobotPosition(sim.getRobot());
                    displayController.getRobotCanvas().redrawRobot(sim.getRobot());
                    // check if the robot is at the goal and stop
                    if (sim.isAtGoal()) {
                        stopSimulator();
                    }
                }
            }
        };
        timer.start();
    }

    /**
     * TODO: Should we return error values?
     */
    public void stopSimulator() {
        if (simulatorRunning) {
            long difference = System.currentTimeMillis() - startTime;
            simulatorRunning = false;
            printText("Stopping Simulator");
            printText("Simulation took approximately " + difference / 1000.0 + " seconds");
            // zero out velocities on the system display
            stopSystemState();
            // update the tabs
            tabController.updateUIs();
            // Kill the timer
            if (timer != null) {
                timer.stop();
                // clean out this bad boy
                timer = null;
            }
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

    /**
     * Updates the system state by reading the current robot status.
     *
     * @param robot Status
     */
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
        wheels.setText("Wheels= One: " + df.format(wheelRates[0]) + ", Two: "
                + df.format(wheelRates[1]) + ", Three: " + df.format(wheelRates[2])
                + ", Four: " + df.format(wheelRates[3]));
    }

    /**
     * Makes sure the wheel rates are what the input was proved, only for InputMode.CONTROL_WHEELS.
     *
     * @param wi Wheel Input
     */
    private void updateWheeledState(WheelInput wi) {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.HALF_UP);
        wheels.setText("Wheels= One: " + df.format(wi.getWheelOne()) + ", Two: "
                + df.format(wi.getWheelTwo()) + ", Three: " + df.format(wi.getWheelThree())
                + ", Four: " + df.format(wi.getWheelFour()));
    }

    /**
     * Resets the system velocity and movement statuses to zero.
     */
    private void stopSystemState() {
        velocityY.setText("Velocity Y: 0.0");
        velocityX.setText("Velocity X: 0.0");
        rotationRate.setText("Rotation Rate: 0.0");
        wheels.setText("Wheels= One: " + 0.0 + ", Two: "
                + 0.0 + ", Three: " + 0.0
                + ", Four: " + 0.0);
    }

    /**
     * Zero's all state fields.
     */
    private void resetSystemState() {
        velocityY.setText("Velocity Y: 0");
        velocityX.setText("Velocity X: 0");
        rotationRate.setText("Rotation Rate: 0");
        direction.setText("Direction: 0");
        position.setText("Position: (0, 0)");
        wheels.setText("Wheels= One: 0, Two: 0, Three: 0, Four: 0");
    }

    public Position getRobotPosition() {
        return robotPosition;
    }

    public boolean isSimulatorRunning() {
        return simulatorRunning;
    }
}
