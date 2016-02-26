package UI;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Background test = new Background(new BackgroundFill(Color.BURLYWOOD, CornerRadii.EMPTY, Insets.EMPTY));
        //displayPane.setBackground(test);
        systemStatePane.setBackground(test);
        // add in the individual controllers
        DisplayPaneController displayControl = new DisplayPaneController(displayPane, gridCanvas, robotCanvas, pathCanvas);
        displayControl.initializePane();
        // add in the tab controller
        TabController tabControl = new TabController(controlPane, this);
        tabControl.init();
    }

    /**
     * TODO: Should we return error values?
     */
    public void startSimulator() {
        setNormalText("Error: Start Not Implemented");
    }

    /**
     * TODO: Should we return error values?
     */
    public void stopSimulator() {
        setNormalText("Error: Stop Not Implemented");
    }

    /**
     * Prints out red text on the output pane.
     * @param text string to print as error
     */
    private void setErrorText(String text) {
        outputTextArea.setStyle("-fx-text-fill: red");
        outputTextArea.setText(outputTextArea.getText() + text + "\n");
    }

    /**
     * Prints out black text on the output pane.
     * @param text string to print
     */
    private void setNormalText(String text) {
        outputTextArea.setStyle("-fx-text-fill: black");
        outputTextArea.setText(outputTextArea.getText() + text + "\n");
    }
}
