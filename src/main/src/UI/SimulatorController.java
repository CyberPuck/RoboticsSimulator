package UI;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import simulator.RobotInput;
import simulator.Simulator;
import utilities.Point;
import utilities.Position;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Primary controller for the entire UI.
 * <p/>
 * Created by CyberPuck on 2016-02-15.
 */
public class SimulatorController implements Initializable {
    @FXML
    private Pane displayPane;

    @FXML
    private TabPane controlPane;

    @FXML
    private Pane systemStatePane;

    // TODO: Should we move these?
    @FXML
    private Canvas gridCanvas;
    @FXML
    private Canvas robotCanvas;
    @FXML
    private Canvas pathCanvas;
    @FXML
    private TextArea outputTextArea;

    private int counter = 0;
    // controller for the simulation display
    private DisplayPaneController displayController;
    private AnimationTimer timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Background test = new Background(new BackgroundFill(Color.BURLYWOOD, CornerRadii.EMPTY, Insets.EMPTY));
        //displayPane.setBackground(test);
        systemStatePane.setBackground(test);
        // add in the individual controllers
        displayController = new DisplayPaneController(displayPane, gridCanvas, robotCanvas, pathCanvas);
        displayController.initializePane();
        // add in the tab controller
        TabController tabControl = new TabController(controlPane, this);
        tabControl.init();
    }

    /**
     * TODO: Should we return error values?
     */
    public void startSimulator(RobotInput input) {
        printText("Starting simulation");
        final Simulator sim = new Simulator(input);
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // update the robot position
                Position currentLocation = displayController.getRobotCanvas().getRobotPosition();
                Position newLoc = sim.calculateNewPosition(currentLocation);
                displayController.getRobotCanvas().setRobotPosition(newLoc);
                displayController.getRobotCanvas().redrawRobot();
            }
        };
        timer.start();
    }

    /**
     * TODO: Should we return error values?
     */
    public void stopSimulator() {
        printText("Stopping Simulator");
        if(timer != null) {
            timer.stop();
            // clean out this bad boy
            timer = null;
        }
    }

    /**
     * Prints out text on the output pane.
     * @param text string to print
     */
    public void printText(String text) {
        outputTextArea.setText(outputTextArea.getText() + text + "\n");
    }
}
