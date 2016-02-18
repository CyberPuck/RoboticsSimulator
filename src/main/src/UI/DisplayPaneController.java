package UI;

import UI.displayCanvases.GridCanvas;
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
        Background background = new Background(new BackgroundFill(Color.BROWN, CornerRadii.EMPTY, Insets.EMPTY));
        displayPane.setBackground(background);
        GridCanvas grid = new GridCanvas(gridCanvas);
        grid.init();
        // set the grid up
        double displayHeight = displayPane.getHeight();
        double displayWidth = displayPane.getWidth();
        System.out.println("System height " + displayHeight + " width " + displayWidth);
    }
}
