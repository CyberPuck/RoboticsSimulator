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
    private TextField speedField;
    @FXML
    private TextField timeToFinish;
    @FXML
    private Button startButton;

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
                    // start the simulation
                    RobotInput input = formatInput();
                    startButton.setText("Stop");
                    controller.startSimulator(input);
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
        speedField.setText("0.0");
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
        double speed = Double.parseDouble(this.speedField.getText());
        double time = Double.parseDouble(this.timeToFinish.getText());
        // get distance between the two points
        double distance = Utils.distanceBetweenPoints(controller.getRobotPosition().getPosition(), endPoint);
        // calculate how long the trip will take (approximately)
        double timeTaken = distance / speed;
        // calculate the rate the robot should rotate
        double rotationRate = 0;
        if (speed != 0) {
            rotationRate = (endOrientation - controller.getRobotPosition().getAngle()) / timeTaken;
        }
        PointInput pi = new PointInput(endPoint, speed, endOrientation, time, rotationRate);
        return pi;
    }
}
