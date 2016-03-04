package UI;

import UI.tabControls.ControlTab;
import UI.tabControls.PathTab;
import UI.tabControls.PointTab;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Initializes the tab pane and all UI controls.
 * <p/>
 * Created by CyberPuck on 2016-02-21.
 */
public class TabController {
    private TabPane tabPane;
    // needed for getting UI to startup simulator
    private SimulatorController controller;

    public TabController(TabPane tabPane, SimulatorController controller) {
        this.tabPane = tabPane;
        this.controller = controller;
    }

    public void init() {
        // add the controller
//        setupTabControllers();
        // Add the control tab (part 1)
        Tab controlTab = new Tab();
        controlTab.setText("Control");
        // load the FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("controlPane.fxml"));
        try {
            Pane testPane = fxmlLoader.load();
            controlTab.setContent(testPane);
            tabPane.getTabs().add(controlTab);
            // add the controller
            fxmlLoader.<ControlTab>getController().setController(controller);
        } catch (IOException e) {
            System.err.println("Failed to load control tab pane");
        }

        // TODO: Add Part 2
        Tab pointTab = new Tab();
        pointTab.setText("Point");
        fxmlLoader = new FXMLLoader(getClass().getResource("pointPane.fxml"));
        try {
            Pane testPane = fxmlLoader.load();
            pointTab.setContent(testPane);
            tabPane.getTabs().add(pointTab);
            fxmlLoader.<PointTab>getController().setController(controller);
        } catch (IOException e) {
            System.err.println("Failed to load point tab pane");
        }

        // TODO: Add Part 3
        Tab pathTab = new Tab();
        pathTab.setText("Path");
        fxmlLoader = new FXMLLoader(getClass().getResource("pathPane.fxml"));
        try {
            Pane testPane = fxmlLoader.load();
            pathTab.setContent(testPane);
            tabPane.getTabs().add(pathTab);
            // add the controller
            fxmlLoader.<PathTab>getController().setController(controller);
        } catch (IOException e) {
            System.err.println("Failed to load path tab pane");
        }

        // TODO: Add Part 4
        Tab wayPointTab = new Tab();
        wayPointTab.setText("Way Points");
        fxmlLoader = new FXMLLoader(getClass().getResource("wayPointPane.fxml"));
        try {
            Pane testPane = fxmlLoader.load();
            wayPointTab.setContent(testPane);
            tabPane.getTabs().add(wayPointTab);
        } catch (IOException e) {
            System.err.println("Failed to load way point tab pane");
        }

        // TODO: Extra Credit?
    }
}
