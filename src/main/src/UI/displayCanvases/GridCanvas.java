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
     *  Setup the grid canvas, one time thing.
     */
    public void init() {
        // TODO: Lines are being drawn incorrectly!
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        // draw them lines
        // default width and height is:
        double width = 360;
        double height = 720;
        gc.setLineWidth(1.0);
        double numberOfPixelsPerInch = 2.0;
        // draw horizontal lines
        for(int i = 0; i < 30; i++) {
            double xPosition = i * (6*numberOfPixelsPerInch);
            gc.moveTo(xPosition, 0);
            gc.lineTo(xPosition, height);
            gc.stroke();
        }
        // draw vertical lines
        for(int i = 0; i < 60; i++) {
            double yPosition = i * (6*numberOfPixelsPerInch);
            gc.moveTo(0, yPosition);
            gc.lineTo(width, yPosition);
            gc.stroke();
        }
    }
}
