package UI.tabControls;

import UI.SimulatorController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import simulator.RobotInput;
import simulator.WheelInput;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Handles the control tab UI.
 * Created by CyberPuck on 2016-02-21.
 */
public class ControlTab implements Initializable {
    private static String ROBOT_IMAGE = "./assets/mecanum_robot_ui.png";

    private static String GENERAL_MODE = "generalMode";
    private static String WHEEL_MODE = "wheelRotationMode";

    // General movement controls (default)
    @FXML
    private ChoiceBox modeBox;
    @FXML
    private Label directionLabel;
    @FXML
    private TextField directionField;
    @FXML
    private Label speedLabel;
    @FXML
    private TextField speedField;
    @FXML
    private Label rotationLabel;
    @FXML
    private TextField rotationField;

    // Wheel based controls
    @FXML
    private Label wheelOneLabel;
    @FXML
    private TextField wheelOneField;
    @FXML
    private Label wheelTwoLabel;
    @FXML
    private TextField wheelTwoField;
    @FXML
    private Label wheelThreeLabel;
    @FXML
    private TextField wheelThreeField;
    @FXML
    private Label wheelFourLabel;
    @FXML
    private TextField wheelFourField;
    @FXML
    private Canvas robotImage;

    // Fire up the simulation
    @FXML
    private Button startButton;
    // flag indicating if the simulator is running
    // TODO: Should this be a flag in the simulator controller?
    private boolean simulatorRunning;

    // needed for simulator controls
    private SimulatorController controller;

    private ArrayList<String> modes = new ArrayList<String>();

    public ControlTab() {
        // fill in the array, this is magically called
        modes.add(GENERAL_MODE);
        modes.add(WHEEL_MODE);
        // simulator is not running on start up?
        simulatorRunning = false;
    }

    /**
     * Sets the primary controller.
     *
     * @param controller used to start/stop the simulator
     */
    public void setController(SimulatorController controller) {
        // add the primary controller
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // add the array
        modeBox.getItems().setAll(modes);
        modeBox.setValue(GENERAL_MODE);
        // add in default values
        initDefaults();
        // enable and disable the fields
        enableGeneralFields();
        // setup the canvas with the robot image
        File fileLocation = new File(ROBOT_IMAGE);
        Image robot = new Image("file:" + fileLocation.getAbsolutePath(), 128, 256, false, true);
        GraphicsContext gc = robotImage.getGraphicsContext2D();
        gc.drawImage(robot, 0, 0);

        // Check for an update in the selection
        modeBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateTab(modes.get(newValue.intValue()));
            }
        });

        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                updateSimulator();
            }
        });
    }

    /**
     * Update the UI based on the mode the UI is in, this is only called when the choice box selection actually changes
     *
     * @param mode mode to switch to for the UI
     */
    private void updateTab(String mode) {
        // based on the mode update the UI elements
        if (mode.equals(GENERAL_MODE)) {
            enableGeneralFields();
        } else if (mode.equals(WHEEL_MODE)) {
            enableWheelFields();
        } else {
            controller.printText("Error: " + mode + " is not supported!");
        }
    }

    private void updateSimulator() {
        if (!simulatorRunning) {
            // TODO: start the simulator
            // setup the input
            RobotInput input = setupInput();
            controller.startSimulator(input);
            startButton.setText("Stop");
            simulatorRunning = true;
        } else {
            // TODO: stop the simulator
            controller.stopSimulator();
            startButton.setText("Start");
            simulatorRunning = false;
        }
    }

    /**
     * Reads the text fields to create the input.
     *
     * @return Wheeled input
     */
    private RobotInput setupInput() {
        double one = Double.parseDouble(wheelOneField.getText());
        double two = Double.parseDouble(wheelTwoField.getText());
        double three = Double.parseDouble(wheelThreeField.getText());
        double four = Double.parseDouble(wheelFourField.getText());
        WheelInput input = new WheelInput(one, two, three, four);
        return input;
    }

    /**
     * General UI Mode fields
     */
    private void disableGeneralFields() {
        directionLabel.setVisible(false);
        directionField.setVisible(false);
        speedLabel.setVisible(false);
        speedField.setVisible(false);
        rotationLabel.setVisible(false);
        rotationField.setVisible(false);
    }

    private void enableGeneralFields() {
        directionLabel.setVisible(true);
        directionField.setVisible(true);
        speedLabel.setVisible(true);
        speedField.setVisible(true);
        rotationLabel.setVisible(true);
        rotationField.setVisible(true);
        // disable the Wheel
        disableWheelFields();
    }

    /**
     * Wheel  UI Mode fields
     */
    private void disableWheelFields() {
        wheelOneLabel.setVisible(false);
        wheelOneField.setVisible(false);
        wheelTwoLabel.setVisible(false);
        wheelTwoField.setVisible(false);
        wheelThreeLabel.setVisible(false);
        wheelThreeField.setVisible(false);
        wheelFourLabel.setVisible(false);
        wheelFourField.setVisible(false);
        robotImage.setVisible(false);
    }

    private void enableWheelFields() {
        wheelOneLabel.setVisible(true);
        wheelOneField.setVisible(true);
        wheelTwoLabel.setVisible(true);
        wheelTwoField.setVisible(true);
        wheelThreeLabel.setVisible(true);
        wheelThreeField.setVisible(true);
        wheelFourLabel.setVisible(true);
        wheelFourField.setVisible(true);
        robotImage.setVisible(true);
        // disable general
        disableGeneralFields();
    }

    /**
     * Setup initial variables so things don't blow up.
     */
    private void initDefaults() {
        // Wheel mode
        wheelOneField.setText("0.0");
        wheelTwoField.setText("0.0");
        wheelThreeField.setText("0.0");
        wheelFourField.setText("0.0");
        // General Mode
        directionField.setText("0.0");
        speedField.setText("0.0");
        rotationField.setText("0.0");
    }
}
