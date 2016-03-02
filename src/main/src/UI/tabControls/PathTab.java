package UI.tabControls;

import UI.SimulatorController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

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
    private TextField speed;
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
}
