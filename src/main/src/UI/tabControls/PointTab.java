package UI.tabControls;

import UI.SimulatorController;
import inputs.PointInput;
import inputs.RobotInput;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import utilities.Point;
import utilities.Utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Handles the point tab UI.
 * <p/>
 * Created by CyberPuck on 2016-02-24.
 */
public class PointTab implements Initializable {
    @FXML
    private TextField xEndPoint;
    @FXML
    private TextField yEndPoint;
    @FXML
    private TextField endOrientation;
    @FXML
    private TextField timeToFinish;
    @FXML
    private Button startButton;
    // way point options
    @FXML
    private TextField oneX;
    @FXML
    private TextField oneY;
    @FXML
    private TextField twoX;
    @FXML
    private TextField twoY;
    @FXML
    private TextField threeX;
    @FXML
    private TextField threeY;

    private SimulatorController controller;

    public PointTab() {
    }

    public void setController(SimulatorController controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (controller.isSimulatorRunning()) {
                    // send the stop command
                    startButton.setText("Start");
                    controller.stopSimulator();
                } else {
                    try {
                        // start the simulation
                        RobotInput input = formatInput();
                        if (input != null) {
                            startButton.setText("Stop");
                            controller.printText("Starting Point Simulation");
                            controller.startSimulator(input);
                        }
                    } catch (Exception e) {
                        controller.printText("Invalid input, only floating point numbers please");
                    }
                }
            }
        });
        // set defaults
        setDefaults();
    }

    /**
     * Sets defaults for the input fields.
     */
    private void setDefaults() {
        xEndPoint.setText("0.0");
        yEndPoint.setText("0.0");
//        speedField.setText("0.0");
        endOrientation.setText("0.0");
        timeToFinish.setText("0.0");
    }


    /**
     * Formats the UI inputs.
     *
     * @return PointInput for the simulator
     */
    private RobotInput formatInput() {
        double x = Double.parseDouble(this.xEndPoint.getText());
        double y = Double.parseDouble(this.yEndPoint.getText());
        Point endPoint = new Point(x, y);
        double endOrientation = Double.parseDouble(this.endOrientation.getText());
        double time = Double.parseDouble(this.timeToFinish.getText());
        if (time == 0) {
            controller.printText("Robot can't complete a path in 0 seconds");
            return null;
        }
        // get distance between the two points
        double distance = Utils.distanceBetweenPoints(controller.getRobotPosition().getPosition(), endPoint);
        double speed = distance / time;
        // calculate the rate the robot should rotate
        double rotationRate = endOrientation / time;
        PointInput pi = new PointInput(endPoint, speed, endOrientation, time, rotationRate);
        // check for way points
        ArrayList<Point> wayPoints = new ArrayList<>();
        if (!oneX.getText().isEmpty()) {
            wayPoints.add(new Point(Double.parseDouble(oneX.getText()), Double.parseDouble(oneY.getText())));
        }
        if (!twoX.getText().isEmpty()) {
            wayPoints.add(new Point(Double.parseDouble(twoX.getText()), Double.parseDouble(twoY.getText())));
        }
        if (!threeX.getText().isEmpty()) {
            wayPoints.add(new Point(Double.parseDouble(threeX.getText()), Double.parseDouble(threeY.getText())));
        }
        pi.setWayPoints(wayPoints);
        return pi;
    }

    /**
     * Checks the controller for state change.
     */
    public void update() {
        if (controller.isSimulatorRunning()) {
            startButton.setText("Stop");
        } else {
            startButton.setText("Start");
        }
    }
}
