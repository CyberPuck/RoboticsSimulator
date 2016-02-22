package UI;

import UI.tabControls.ControlTab;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Initializes the tab pane and all UI controls.
 *
 * Created by CyberPuck on 2016-02-21.
 */
public class TabController {
    private TabPane tabPane;

    public TabController(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public void init() {
        // create the tabs
        Tab directionTab = new Tab();
        directionTab.setText("ControlTest");

        tabPane.getTabs().add(directionTab);

        // Add the control tab (part 1)
        Tab tester = new Tab();
        tester.setText("FXML test");
        // load the FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("controlPane.fxml"));
        try {
            Pane testPane = fxmlLoader.load();
            tester.setContent(testPane);
            tabPane.getTabs().add(tester);
        } catch (IOException e) {
            System.err.println("Failed to load control tab pane");
        }

        // TODO: Add Part 2
        // TODO: Add Part 3
        // TODO: Add Part 4
        // TODO: Extra Credit?
    }
}
