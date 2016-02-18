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
    private ScrollPane outputPane;

    @FXML
    private Pane systemStatePane;

    // TODO: Can we move these?
    @FXML
    private Canvas gridCanvas;
    @FXML
    private Canvas robotCanvas;
    @FXML
    private Canvas pathCanvas;
    @FXML
    private Button testerButton;
    @FXML
    private TextArea outputTextArea;

    private int counter = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        testerButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                outputTextArea.setText(outputTextArea.getText() + (++counter) + ": This is a test!\n");
            }
        });
        Background test = new Background(new BackgroundFill(Color.BURLYWOOD, CornerRadii.EMPTY, Insets.EMPTY));
        //displayPane.setBackground(test);
        systemStatePane.setBackground(test);
        // add in the individual controllers
        DisplayPaneController displayControl = new DisplayPaneController(displayPane);
        displayControl.initializePane();
    }
}
