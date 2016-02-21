package UI.displayCanvases;

import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
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
        GraphicsContext gc = pathCanvas.getGraphicsContext2D();
        pathCanvas.setOnMouseDragEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!createPathMode) {
                    System.out.println("Trying to draw line");
                    createPathMode = true;
                    // start creating the path
//                    System.out.println(event.getX() + ", " + event.getY());
                    userPath = new Path(event.getX(), event.getY());
                    drawPath.setStartX(event.getX());
                    drawPath.setStartY(event.getY());
                } else {
                    // error?
                }
            }
        });

        pathCanvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(createPathMode) {
                    drawPath.setEndX(event.getX());
                    drawPath.setEndY(event.getY());
                } else {
                    System.out.println("Not doing nuttin");
                }
            }
        });

        pathCanvas.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
            @Override
            public void handle(MouseDragEvent event) {
                if(createPathMode) {
                    createPathMode = false;
                    drawPath.setEndX(event.getX());
                    drawPath.setEndY(event.getY());
                    userPath.addEndPoint(event.getX(), event.getY());
                    System.out.println("Path complete:" + userPath.toString());
                }
            }
        });
    }
}
