package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * A utility class to provide a standardized way to display alerts to the user.
 */
public class UIUtils {

    /**
     * Displays a standard alert to the user with a custom title and message.
     *
     * @param title   The title of the alert dialog.
     * @param message The message to be displayed in the alert dialog.
     */
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays an information alert with a predefined title "Information".
     *
     * @param message The informative message to be displayed in the alert dialog.
     */
    public static void showInfoAlert(String message) {
        showAlert("Information", message);
    }

    /**
     * Displays an error alert with a predefined title "Error".
     *
     * @param message The error message to be displayed in the alert dialog.
     */
    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Displays a confirmation alert with a custom title and message.
     * A confirmation alert typically asks the user to make a decision.
     *
     * @param title   The title of the confirmation dialog.
     * @param message The message to be displayed, usually asking for user confirmation.
     * @return An Optional containing the user's response (ButtonType).
     */
    public static Optional<ButtonType> showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }

}
