package UI.tabControls;

import UI.SimulatorController;
import inputs.CirclePathInput;
import inputs.FigureEightPathInput;
import inputs.RectanglePathInput;
import inputs.RobotInput;
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
import javafx.scene.input.MouseEvent;
import utilities.Point;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Handles the path tab UI.
 * <p/>
 * Created by CyberPuck on 2016-02-24.
 */
public class PathTab implements Initializable {
    // Modes as strings
    private static String CIRCLE = "Circle";
    private static String RECTANGLE = "Rectangle";
    private static String FIGURE_EIGHT = "Figure Eight";

    @FXML
    private ChoiceBox pathMode;

    // circle controls
    @FXML
    private Label radiusLabel;
    @FXML
    private TextField circleRadius;

    // rectangle controls
    @FXML
    private TextField rectangleTop;
    @FXML
    private TextField rectangleSide;
    @FXML
    private Canvas rectangleCanvas;

    // figure eight controls
    @FXML
    private Label closeRadiusLabel;
    @FXML
    private TextField closeRadius;
    @FXML
    private Label farRadiusLabel;
    @FXML
    private TextField farRadius;

    // common controls
    @FXML
    private TextField inclination;
    @FXML
    private TextField time;
    @FXML
    private TextField rotation;

    // Simulator start/stop
    @FXML
    private Button startButton;

    // simulator controls
    private SimulatorController controller;

    private ArrayList<String> modes = new ArrayList<String>();

    public PathTab() {
        modes.add(CIRCLE);
        modes.add(RECTANGLE);
        modes.add(FIGURE_EIGHT);
    }

    public void setController(SimulatorController controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        pathMode.getItems().setAll(modes);
        pathMode.setValue(CIRCLE);
        // setup the UI
        enableCircle();
        // draw the rectangle
        GraphicsContext gc = rectangleCanvas.getGraphicsContext2D();
        gc.setLineWidth(10.0);
        gc.strokeRect(0, 0, 75, 100);
//        gc.stroke();

        // check for an update to the choice box
        pathMode.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateTab(modes.get(newValue.intValue()));
            }
        });

        // Button pressed
        startButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (controller.isSimulatorRunning()) {
                    controller.printText("Stopping Simulation");
                    controller.stopSimulator();
                    startButton.setText("Start");
                } else {
                    updateSimulator();
                }
            }
        });

        // set defaults
        setDefaults();
    }

    private void updateSimulator() {
        RobotInput input;
        String mode;
        if (pathMode.getValue().equals(CIRCLE)) {
            input = setupCircleInput();
            mode = "Starting Circle Path Simulation";
        } else if (pathMode.getValue().equals(RECTANGLE)) {
            input = setupRectangleInput();
            mode = "Starting Rectangle Path Simulation";
        } else {
            input = setupFigureEightInput();
            mode = "Starting Figure Eight Path Simulation";
        }
        if (input != null) {
            controller.printText(mode);
            controller.startSimulator(input);
            startButton.setText("Stop");
        }
    }

    /**
     * Given the UI inputs, get the input for the circle.
     *
     * @return CirclePathInput
     */
    private RobotInput setupCircleInput() {
        Point origin = controller.getRobotPosition().getPosition();
        double radius = Double.parseDouble(circleRadius.getText());
        double angle = Double.parseDouble(inclination.getText());
        double endAngle = Double.parseDouble(rotation.getText());
        double time = Double.parseDouble(this.time.getText());
        if (time == 0) {
            controller.printText("Robot can't complete a path in 0 seconds");
            return null;
        }
        double rr = (endAngle - controller.getRobotPosition().getAngle()) / time;
        return new CirclePathInput(origin, radius, angle, endAngle, rr, time);
    }

    /**
     * Given the UI inputs, get the input for the rectangle.
     *
     * @return RectanglePathInput
     */
    private RobotInput setupRectangleInput() {
        Point origin = controller.getRobotPosition().getPosition();
        double top = Double.parseDouble(rectangleTop.getText());
        double side = Double.parseDouble(rectangleSide.getText());
        double angle = Double.parseDouble(inclination.getText());
        double endAngle = Double.parseDouble(rotation.getText());
        double time = Double.parseDouble(this.time.getText());
        if (time == 0) {
            controller.printText("Robot can't complete a path in 0 seconds");
            return null;
        }
        double rr = (endAngle - controller.getRobotPosition().getAngle()) / time;
        return new RectanglePathInput(origin, top, side, angle, endAngle, rr, time);
    }

    /**
     * Given the UI inputs, get the input for the figure eight.
     *
     * @return FigureEightPathInput
     */
    private RobotInput setupFigureEightInput() {
        Point origin = controller.getRobotPosition().getPosition();
        double radiusOne = Double.parseDouble(closeRadius.getText());
        double radiusTwo = Double.parseDouble(farRadius.getText());
        double angle = Double.parseDouble(inclination.getText());
        double endAngle = Double.parseDouble(rotation.getText());
        double time = Double.parseDouble(this.time.getText());
        if (time == 0) {
            controller.printText("Robot can't complete a path in 0 seconds");
            return null;
        }
        double rr = (endAngle - controller.getRobotPosition().getAngle()) / time;
        return new FigureEightPathInput(origin, radiusOne, radiusTwo, angle, endAngle, rr, time);
    }

    private void updateTab(String mode) {
        // update UI based on mode selected
        if (mode.equals(CIRCLE)) {
            enableCircle();
        } else if (mode.equals(RECTANGLE)) {
            enableRectangle();
        } else if (mode.equals(FIGURE_EIGHT)) {
            enableFigureEight();
        } else {
            controller.printText("Error: " + mode + " is not supported!");
        }
    }

    private void enableCircle() {
        radiusLabel.setVisible(true);
        circleRadius.setVisible(true);
        // disable the remaining modes
        disableRectangle();
        disableFigureEight();
    }

    private void disableCircle() {
        radiusLabel.setVisible(false);
        circleRadius.setVisible(false);
    }

    private void enableRectangle() {
        rectangleTop.setVisible(true);
        rectangleSide.setVisible(true);
        rectangleCanvas.setVisible(true);
        disableCircle();
        disableFigureEight();
    }

    private void disableRectangle() {
        rectangleTop.setVisible(false);
        rectangleSide.setVisible(false);
        rectangleCanvas.setVisible(false);
    }

    private void enableFigureEight() {
        closeRadiusLabel.setVisible(true);
        closeRadius.setVisible(true);
        farRadiusLabel.setVisible(true);
        farRadius.setVisible(true);
        disableCircle();
        disableRectangle();
    }

    private void disableFigureEight() {
        closeRadiusLabel.setVisible(false);
        closeRadius.setVisible(false);
        farRadiusLabel.setVisible(false);
        farRadius.setVisible(false);
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

    /**
     * Set all UI elements to a default value.
     */
    private void setDefaults() {
        circleRadius.setText("0.0");
        rectangleSide.setText("0.0");
        rectangleTop.setText("0.0");
        closeRadius.setText("0.0");
        farRadius.setText("0.0");
        inclination.setText("0.0");
        rotation.setText("0.0");
        time.setText("0.0");
    }
}
