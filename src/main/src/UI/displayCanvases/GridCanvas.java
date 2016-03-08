package UI.displayCanvases;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Handles drawing the grid overlay.
 * Created by CyberPuck on 2016-02-17.
 */
public class GridCanvas {
    private Canvas gridCanvas;

    public GridCanvas(Canvas gridCanvas) {
        this.gridCanvas = gridCanvas;
    }

    /**
     * Setup the grid canvas, one time thing.
     */
    public void init() {
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();

        // draw them lines
        // default width and height is:
        double width = 360;
        double height = 720;
        double numberOfPixelsPerInch = 2.0;
        // draw horizontal lines
        for (int i = 0; i < 30; i++) {
            double xPosition = i * (6 * numberOfPixelsPerInch);
            // Add fudge factor to make 1 pixel wide lines
            xPosition += 0.5;
            gc.setLineWidth(0.0);
            gc.strokeLine(xPosition, 0, xPosition, height);
        }
        // draw vertical lines
        for (int i = 0; i < 60; i++) {
            double yPosition = i * (6 * numberOfPixelsPerInch);
            // Add fudge factor to make 1 pixel wide lines
            yPosition += 0.5;
            gc.setLineWidth(0.0);
            gc.strokeLine(0, yPosition, width, yPosition);
        }
    }
}
