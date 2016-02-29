package UI.displayCanvases;

import javafx.scene.canvas.Canvas;
import javafx.scene.shape.Line;
import utilities.Path;

/**
 * Handles drawing the paths overlay.
 * Created by CyberPuck on 2016-02-17.
 */
public class PathCanvas {
    private Canvas pathCanvas;
    private boolean createPathMode = false;
    private Path userPath;
    private Path tempPath; // used to draw path during click and drag
    Line drawPath = new Line();

    public PathCanvas(Canvas pathCanvas) {
        this.pathCanvas = pathCanvas;
    }

    public void init() {
//        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
//        gc.setLineWidth(0.0);
//        gc.setStroke(Color.LIME);
//        gc.strokeLine(100,0, 100, 100);
    }

    /**
     * Function to draw a path on the canvas
     */
    public void drawPath() {
        // TODO: fill this in, take into account relocation if the boundary is hit
    }
}
