package c195.c195wgu;

import utils.SceneChanger;
import helper.JDBC;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {

    private static final String INITIAL_SCENE = "/view/LoginView.fxml";
    private static final String WINDOW_TITLE = "Appointment System";
    private static final double WINDOW_WIDTH = 320;
    private static final double WINDOW_HEIGHT = 240;

    private static Connection connection;
    @Override
    public void start(Stage stage) {
        try {
            // Use the SceneChanger utility to set the initial scene
            SceneChanger.setInitialScene(stage, INITIAL_SCENE, WINDOW_TITLE, WINDOW_WIDTH, WINDOW_HEIGHT);
        } catch (IOException e) {
            // The error alert will be shown by SceneChanger, but you can also handle or log the exception here if needed
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        connection = JDBC.openConnection(); // Open the connection and store it
        launch();
    }

    @Override
    public void stop() throws Exception {
        JDBC.closeConnection(connection); // Close the connection when the application terminates
    }
}
