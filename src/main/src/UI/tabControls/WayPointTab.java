package UI.tabControls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Handles the way point tab UI.
 * <p/>
 * Created by CyberPuck on 2016-02-24.
 */
public class WayPointTab {
    @FXML
    private TextField xEndPoint;
    @FXML
    private TextField yEndPoint;
    @FXML
    private TextArea wayPoints;
    @FXML
    private TextField rotation;
    @FXML
    private TextField speed;
    @FXML
    private TextField time;
    @FXML
    private Button startButton;

    public WayPointTab() {
    }
}
