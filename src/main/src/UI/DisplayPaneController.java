package UI;

import javafx.geometry.Insets;
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

    private Pane displayPane;

    /**
     * Setup the displayPane.
     * @param displayPane
     */
    public DisplayPaneController(Pane displayPane){
        this.displayPane = displayPane;
    }

    /**
     * Draw the background color (brown) with the 6" x 6" grid (grey).
     */
    public void initializePane() {
        // set the background color
        Background background = new Background(new BackgroundFill(Color.BROWN, CornerRadii.EMPTY, Insets.EMPTY));
        displayPane.setBackground(background);
        // set the grid up
        double displayHeight = displayPane.getHeight();
        double displayWidth = displayPane.getWidth();
        System.out.println("System height " + displayHeight + " width " + displayWidth);
    }
}
