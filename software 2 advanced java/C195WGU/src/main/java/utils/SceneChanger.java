package utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneChanger {

    /**
     * Change the scene for a given window based on FXML file path.
     *
     * @param event Source action event from the UI control that triggered the change.
     * @param fxmlFilePath The path to the FXML file for the new scene.
     * @throws IOException If there's an issue loading the FXML.
     */
    public static void changeScene(javafx.event.ActionEvent event, String fxmlFilePath) throws IOException {
        try {
            URL resource = SceneChanger.class.getResource(fxmlFilePath);
            if (resource == null) {
                throw new IOException("Resource not found: " + fxmlFilePath);
            }

            Parent newRoot = FXMLLoader.load(resource);
            Scene scene = new Scene(newRoot);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            UIUtils.showErrorAlert("An error occurred while changing the scene to: " + fxmlFilePath);
            throw e;
        }
    }


    /**
     * Sets the initial scene for the primary stage.
     *
     * @param stage The primary stage.
     * @param fxmlFilePath The path to the FXML file for the initial scene.
     * @param title The title of the window.
     * @param width The width of the window.
     * @param height The height of the window.
     * @throws IOException If there's an issue loading the FXML.
     */
    public static void setInitialScene(Stage stage, String fxmlFilePath, String title, double width, double height) throws IOException {
        try {
            URL resource = SceneChanger.class.getResource(fxmlFilePath);
            if (resource == null) {
                throw new IOException("Resource not found: " + fxmlFilePath);
            }

            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root, width, height);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            UIUtils.showErrorAlert("An error occurred while setting the initial scene to: " + fxmlFilePath);
            throw e;
        }
    }


}
