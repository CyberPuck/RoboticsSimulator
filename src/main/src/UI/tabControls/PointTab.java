package UI.tabControls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Handles the point tab UI.
 *
 * Created by CyberPuck on 2016-02-24.
 */
public class PointTab {
    @FXML
    private TextField xEndPoint;
    @FXML
    private TextField yEndPoint;
    @FXML
    private TextField endOrientation;
    @FXML
    private TextField speed;
    @FXML
    private TextField timeToFinish;
    @FXML
    private Button startButton;

    public PointTab() {}
}
