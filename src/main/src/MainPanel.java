//package helloworld;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainPanel extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Robot Simulator");
            Pane myPane = (Pane) FXMLLoader.load(getClass().getResource
                    ("mainPanel.fxml"));
            Scene myScene = new Scene(myPane);
            primaryStage.setScene(myScene);
            primaryStage.show();
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}