package UI;

import UI.displayCanvases.GridCanvas;
import UI.displayCanvases.PathCanvas;
import UI.displayCanvases.RobotCanvas;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Handles calls to the DisplayPaneController.  This will show the robot in motion along with the defaults and paths.
 *
 * Created by CyberPuck on 2016-02-15.
 */
public class DisplayPaneController {

    private Canvas gridCanvas;
    private Canvas robotCanvas;
    private Canvas pathCanvas;

    private Pane displayPane;

    // Controllers
    private RobotCanvas robot;
    private PathCanvas path;

    /**
     * Setup the displayPane.
     * @param displayPane
     */
    public DisplayPaneController(Pane displayPane, Canvas gridCanvas, Canvas robotCanvas, Canvas pathCanvas){
        this.displayPane = displayPane;
        this.gridCanvas = gridCanvas;
        this.robotCanvas = robotCanvas;
        this.pathCanvas = pathCanvas;
    }

    /**
     * Draw the background color (brown) with the 6" x 6" grid (grey).
     */
    public void initializePane() {
        // set the background color
        Background background = new Background(new BackgroundFill(Color.rgb(136, 84, 49), CornerRadii.EMPTY, Insets.EMPTY));
        displayPane.setBackground(background);
        GridCanvas grid = new GridCanvas(gridCanvas);
        // setup the grid
        grid.init();
        // TODO: setup the robot
        robot = new RobotCanvas(robotCanvas);
        robot.init();
        // Setup the path stuff
        path = new PathCanvas(pathCanvas);
        path.init();
    }

    public RobotCanvas getRobotCanvas() {
        return this.robot;
    }
}
