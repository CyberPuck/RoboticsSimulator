package UI;

import UI.displayCanvases.GridCanvas;
import UI.displayCanvases.PathCanvas;
import UI.displayCanvases.RobotCanvas;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import utilities.Point;

/**
 * Handles calls to the DisplayPaneController.  This will show the robot in motion along with the defaults and paths.
 * <p/>
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
     * Stores all major display components.
     *
     * @param displayPane Holds all canvases, displays background color
     * @param gridCanvas Displays the 6" x 6" grid
     * @param robotCanvas Displays the robot
     * @param pathCanvas Displays both the robot and input paths
     */
    public DisplayPaneController(Pane displayPane, Canvas gridCanvas, Canvas robotCanvas, Canvas pathCanvas) {
        this.displayPane = displayPane;
        this.gridCanvas = gridCanvas;
        this.robotCanvas = robotCanvas;
        this.pathCanvas = pathCanvas;
    }

    /**
     * Draw the background color (brown) with the 6" x 6" grid (grey).
     *
     * @param origin Origin of the canvases (in the GRF in feet)
     */
    public void initializePane(Point origin) {
        // set the background color
        Background background = new Background(new BackgroundFill(Color.rgb(136, 84, 49), CornerRadii.EMPTY, Insets.EMPTY));
        displayPane.setBackground(background);
        GridCanvas grid = new GridCanvas(gridCanvas);
        // setup the grid
        grid.init();
        // Setup the path stuff
        path = new PathCanvas(pathCanvas);
        // setup the robot
        robot = new RobotCanvas(robotCanvas, path, origin);
        robot.init();
    }

    public RobotCanvas getRobotCanvas() {
        return this.robot;
    }

    public PathCanvas getPathCanvas() {
        return path;
    }
}
