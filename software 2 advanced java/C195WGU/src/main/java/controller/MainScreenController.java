package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import utils.UIUtils;
import utils.SceneChanger;

import java.io.IOException;
import java.awt.Desktop;
import java.io.File;

public class MainScreenController {

    @FXML
    private Button appointmentsButton;
    @FXML
    private Button customersButton;
    @FXML
    private Button reportsButton;
    @FXML
    private Button logsButton;
    @FXML
    private Button exitButton;

    @FXML
    void handleAppointments(ActionEvent event) throws IOException {
        SceneChanger.changeScene(event, "/view/AppointmentScreen.fxml");
    }

    @FXML
    void handleCustomers(ActionEvent event) throws IOException {
        SceneChanger.changeScene(event, "/view/CustomerRecords.fxml");
    }

    @FXML
    void handleReports(ActionEvent event) throws IOException {
        SceneChanger.changeScene(event, "/view/ReportDashboard.fxml");
    }


    @FXML
    public void handleLogs() {
        File file = new File("login_activity.txt");

        if(file.exists()) {
            if(Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    System.out.println("Error Opening Log File: " + e.getMessage());
                    // Display a user-friendly error dialog
                    UIUtils.showErrorAlert("Error opening log file.");
                }
            } else {
                // Handle the scenario where the Desktop API isn't supported
                System.out.println("Desktop operations are not supported on this platform.");
                UIUtils.showErrorAlert("Unable to open log file on this platform.");
            }
        } else {
            System.out.println("Log file does not exist.");
            // Show an alert to inform the user
            UIUtils.showErrorAlert("Log file does not exist.");
        }
    }

    @FXML
    void handleExit(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
