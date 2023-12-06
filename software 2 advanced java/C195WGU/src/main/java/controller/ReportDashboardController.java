package controller;

import helper.AppointmentsDAO;
import helper.ContactsDAO;
import helper.ReportsDAO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;
import utils.SceneChanger;
import utils.UIUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;

/**
 * Class and methods to display 3 reports.
 */
public class ReportDashboardController {

    @FXML
    public Button btnBackToMainMenu;
    @FXML
    private ComboBox<String> cmbContacts;
    @FXML
    private TableView<Appointments> tblAppointments;
    @FXML
    private TableColumn<Appointments, Integer> colAppointmentID;
    @FXML
    private TableColumn<Appointments, String> colTitle, colDescription, colLocation, colContact, colType;
    @FXML
    private TableColumn<Appointments, LocalDateTime> colStartDateTime, colEndDateTime;
    @FXML
    private TableColumn<Appointments, Integer> colCustomerID;
    @FXML
    private TableView<TypeReport> tblAppointmentsByType;
    @FXML
    private TableColumn<TypeReport, String> colAppointmentType;
    @FXML
    private TableColumn<TypeReport, Integer> colTypeTotal;
    @FXML
    private TableView<MonthlyReport> tblAppointmentsByMonth;
    @FXML
    private TableColumn<MonthlyReport, String> colMonth;
    @FXML
    private TableColumn<MonthlyReport, Integer> colMonthTotal;
    @FXML
    private TableView<CountryReport> tblCustomersByCountry;
    @FXML
    private TableColumn<CountryReport, String> colCountryName;
    @FXML
    private TableColumn<CountryReport, Integer> colCustomerCount;

    /**
     * Initializes the various UI components, including table configurations and populating combo boxes.
     */
    public void initialize() {

        System.out.println(" Report's Initialize method called");



        setupCustomersByCountryTable();
        setupAppointmentsTable();
        setupAppointmentsByTypeTable();
        setupAppointmentsByMonthTable();
        populateContactsComboBox();
    }

    /**
     * Configures the 'Customers by Country' table by setting appropriate cell value factories.
     */
    private void setupCustomersByCountryTable() {
        colCountryName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustomerCount.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    /**
     * Configures the 'Appointments' table by setting appropriate cell value factories.
     */
    private void setupAppointmentsTable() {
        colAppointmentID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colStartDateTime.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        colEndDateTime.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        colCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactId"));  // Assuming it maps to contactID
    }

    /**
     * Configures the 'Appointments by Type' table by setting appropriate cell value factories.
     */
    private void setupAppointmentsByTypeTable() {
        colAppointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colTypeTotal.setCellValueFactory(new PropertyValueFactory<>("count"));
    }

    /**
     * Configures the 'Appointments by Month' table by setting appropriate cell value factories.
     */
    private void setupAppointmentsByMonthTable() {
        colMonth.setCellValueFactory(new PropertyValueFactory<>("month"));
        colMonthTotal.setCellValueFactory(new PropertyValueFactory<>("totalAppointments"));
    }

    /**
     * Populates the Contacts ComboBox with data fetched from the database.
     * Handles potential SQL exceptions and displays an error message to the user if necessary.
     */
    private void populateContactsComboBox() {
        try {
            ObservableList<Contacts> contactsObservableList = ContactsDAO.getAllContacts();
            ObservableList<String> allContactsNames = FXCollections.observableArrayList();
            contactsObservableList.forEach(contact -> allContactsNames.add(contact.getContactName()));
            cmbContacts.setItems(allContactsNames);
        } catch (SQLException e) {
            UIUtils.showErrorAlert("There was a problem fetching the contacts. Please try again later.");
        }
    }


    /**
     * Populates the Appointments table with data for the selected contact.
     */
    @FXML
    public void populateAppointmentsByContact() {
        try {
            int contactID = 0;

            ObservableList<Appointments> allAppointmentData = AppointmentsDAO.getAllAppointments();
            ObservableList<Appointments> filteredAppointments = FXCollections.observableArrayList();
            ObservableList<Contacts> allContacts = ContactsDAO.getAllContacts();

            String selectedContactName = cmbContacts.getSelectionModel().getSelectedItem();

            // Identify the ID of the selected contact
            for (Contacts contact : allContacts) {
                if (selectedContactName.equals(contact.getContactName())) {
                    contactID = contact.getContactId();
                    break;
                }
            }

            // Filter all appointments that match the contact's ID
            for (Appointments appointment : allAppointmentData) {
                if (appointment.getContactId() == contactID) {
                    filteredAppointments.add(appointment);
                }
            }

            // Update the Appointments TableView
            tblAppointments.setItems(filteredAppointments);

        } catch (SQLException e) {
            UIUtils.showErrorAlert("There was a problem fetching the appointments by contact. Please try again later.");
        }
    }

    /**
     * Event handler for the selection of the "Appointment Totals" tab. This method is invoked when the "Appointment Totals"
     * tab is selected. It triggers the population of appointment totals data if the tab is being actively selected.
     *
     * @param e The Event object representing the selection event of the tab.
     */
    @FXML
    private void onAppointmentTotalsTabSelected(Event e) {
        if (((Tab) e.getSource()).isSelected()) {
            try {
                populateAppointmentTotals();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Populates appointment totals data for display in the "Appointment Totals" section.
     * This method retrieves all appointments from the database and generates summary reports based on appointment types and months.
     *
     * @throws SQLException If a database access error occurs while retrieving appointments.
     */
    @FXML
    public void populateAppointmentTotals() throws SQLException {
        ObservableList<Appointments> allAppointments = AppointmentsDAO.getAllAppointments();
        ObservableList<Month> allMonths = FXCollections.observableArrayList();
        ObservableList<Month> uniqueMonths = FXCollections.observableArrayList();
        ObservableList<String> allTypes = FXCollections.observableArrayList();
        ObservableList<String> uniqueTypes = FXCollections.observableArrayList();

        // Collect all types and months
        allAppointments.forEach(appointment -> {
            allTypes.add(appointment.getType());
            allMonths.add(appointment.getStartDateTime().getMonth());
        });

        // Get unique months and types
        allMonths.stream().distinct().forEach(uniqueMonths::add);
        allTypes.stream().distinct().forEach(uniqueTypes::add);

        // Populate type-based report
        ObservableList<TypeReport> typeReport = FXCollections.observableArrayList();
        for (String type : uniqueTypes) {
            int count = Collections.frequency(allTypes, type);
            typeReport.add(new TypeReport(type, count));
        }

        // Populate month-based report
        ObservableList<MonthlyReport> monthReport = FXCollections.observableArrayList();
        for (Month month : uniqueMonths) {
            int count = Collections.frequency(allMonths, month);
            monthReport.add(new MonthlyReport(month.name(), count));
        }

        // Bind the data to the tables
        tblAppointmentsByType.setItems(typeReport);
        tblAppointmentsByMonth.setItems(monthReport);
    }

    /**
     * Event handler for the selection of the "Customers by Country" tab. This method is invoked when the "Customers by Country"
     * tab is selected. It triggers the retrieval and display of customer country summary data if the tab is being actively selected.
     *
     * @param event The Event object representing the selection event of the tab.
     */
    @FXML
    private void populateCustomersByCountry(Event event) {
        if (((Tab) event.getSource()).isSelected()) {
            try {
                fetchAndSetCountryReportData();
            } catch (SQLException ex) {
                UIUtils.showErrorAlert("There was a problem fetching customers by country. Please try again later.");
            }
        }
    }

    /**
     * Fetches customer country summary data and sets it in the associated UI table.
     * This method retrieves customer country summary data using ReportsDAO and populates it in the table for display.
     *
     * @throws SQLException If a database access error occurs while retrieving customer data.
     */
    private void fetchAndSetCountryReportData() throws SQLException {
        ObservableList<CountryReport> countryReportList = ReportsDAO.fetchCountrySummary();

        tblCustomersByCountry.setItems(countryReportList);
    }



    @FXML
    public void handleBackToMainMenu(ActionEvent event) throws IOException {
        SceneChanger.changeScene(event, "/view/MainScreen.fxml");
    }
}
