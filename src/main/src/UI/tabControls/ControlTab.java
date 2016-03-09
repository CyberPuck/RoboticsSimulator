package UI.tabControls;

import UI.SimulatorController;
import inputs.GeneralInput;
import inputs.RobotInput;
import inputs.WheelInput;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import robot.Kinematics;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Handles the control tab UI.
 * Created by CyberPuck on 2016-02-21.
 */
public class ControlTab implements Initializable {
    // Labels for the choice box
    private static String GENERAL_MODE = "General";
    private static String WHEEL_MODE = "Wheel Rotation";

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

    // needed for simulator controls
    private SimulatorController controller;

    private ArrayList<String> modes = new ArrayList<String>();

    public ControlTab() {
        // fill in the array, this is magically called
        modes.add(GENERAL_MODE);
        modes.add(WHEEL_MODE);
    }

    private void setDefaults() {
        this.directionField.setText("0.0");
        this.rotationField.setText("0.0");
        this.speedField.setText("0.0");
        this.wheelOneField.setText("0.0");
        this.wheelTwoField.setText("0.0");
        this.wheelThreeField.setText("0.0");
        this.wheelFourField.setText("0.0");
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
        Image robot = new Image(getClass().getResource("/assets/mecanum_robot_ui.png").toString(), 128, 256, false, true);
        GraphicsContext gc = robotImage.getGraphicsContext2D();
        gc.drawImage(robot, 0, 0);
        // Check for an update in the selection
        modeBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateTab(modes.get(newValue.intValue()));
            }
        });
        // handle start button press
        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                updateSimulator();
            }
        });
        // set default values
        setDefaults();
        // handle key pressed on the start button
        startButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
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
        if (!this.controller.isSimulatorRunning()) {
            // setup the input
            RobotInput input;
            String mode;
            if (modeBox.getValue().equals(GENERAL_MODE)) {
                input = setupGeneralInput();
                mode = "Starting General Control Simulation";
            } else { // assume wheel mode
                input = setupWheelInput();
                mode = "Starting Wheel Control Simulation";
            }
            // verify the input is valid
            if (input != null) {
                controller.printText(mode);
                controller.startSimulator(input);
                startButton.setText("Stop");
            }
        } else {
            controller.stopSimulator();
            startButton.setText("Start");
        }
    }

    /**
     * Reads the text fields to create the input.
     *
     * @return Wheeled input
     */
    private RobotInput setupWheelInput() {
        double one = Double.parseDouble(wheelOneField.getText());
        double two = Double.parseDouble(wheelTwoField.getText());
        double three = Double.parseDouble(wheelThreeField.getText());
        double four = Double.parseDouble(wheelFourField.getText());
        double velX = Kinematics.calculateVelocityX(controller.getRadius(), one, two, three, four);
        double velY = Kinematics.calculateVelocityY(controller.getRadius(), one, two, three, four);
        double totalVel = Math.sqrt(velX * velX + velY * velY);
        if (totalVel > 15.0) {
            controller.printText("Robot speed cannot exceed 15.0 ft/s, current input results in: " + Math.round(totalVel) + " ft/s");
            return null;
        }
        return new WheelInput(one, two, three, four);
    }

    /**
     * Reads the text fields to create the input.
     *
     * @return General input
     */
    private RobotInput setupGeneralInput() {
        double direction = Double.parseDouble(directionField.getText());
        double speed = Double.parseDouble(speedField.getText());
        if (speed > 15.0) {
            controller.printText("Robot speed cannot exceed 15.0 ft/s, current input results in: " + speed + " ft/s");
            return null;
        }
        double rotation = Double.parseDouble(rotationField.getText());
        GeneralInput gi = new GeneralInput(direction, speed, rotation);
        gi.setStartLocation(controller.getRobotPosition().getPosition());
        return gi;
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
