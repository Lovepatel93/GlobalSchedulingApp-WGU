package controller;


import helper.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.ActionEvent;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointments;
import model.Contacts;
import model.Customers;
import model.User;
import utils.SceneChanger;
import utils.TimeConversionUtil;
import utils.UIUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static helper.ContactsDAO.findContactID;

public class AppointmentScreenController {

    // TableView and TableColumns
    @FXML private TableView<Appointments> appointmentsTable;
    @FXML private TableColumn<Appointments, Integer> appointmentID;
    @FXML private TableColumn<Appointments, String> appointmentTitle;
    @FXML private TableColumn<Appointments, String> appointmentDescription;
    @FXML private TableColumn<Appointments, String> appointmentLocation;
    @FXML private TableColumn<Appointments, String> appointmentType;
    @FXML private TableColumn<Appointments, LocalDateTime> appointmentStart;
    @FXML private TableColumn<Appointments, LocalDateTime> appointmentEnd;
    @FXML private TableColumn<Appointments, Integer> appointmentCustomerID;
    @FXML private TableColumn<Appointments, Integer> appointmentUserID;
    @FXML private TableColumn<Appointments, Integer> appointmentContact;

    // RadioButtons for view selector
    @FXML private RadioButton weeklyViewRadio;
    @FXML private RadioButton monthlyViewRadio;
    @FXML private RadioButton allViewRadio;
    @FXML private ToggleGroup viewToggleGroup;

    // Buttons
    @FXML private Button addNewAppointment;
    @FXML private Button removeAppointment;
    @FXML private Button updateExistingAppointment;
    @FXML private Button saveAppointmentButton;
    @FXML private Button returnToMain;

    // TextFields for appointment details
    @FXML private TextField appointmentIDField;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField locationField;
    @FXML private TextField typeField;

    @FXML private ComboBox<Integer> customerIDComboBox;
    @FXML private ComboBox<Integer> userIDComboBox;


    // DatePickers and ComboBoxes for start and end times
    @FXML private DatePicker startDatePicker;
    @FXML private ComboBox<String> startTimeCombo;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> endTimeCombo;
    @FXML private ComboBox<String> contactCombo;

    private List<String> times;

    @FXML
    public void initialize() {
        System.out.println("Initializing Appointments screen...");

        //addTestColumn();

        // Set up the table columns with appropriate values from the Appointments model
        appointmentID.setCellValueFactory(new PropertyValueFactory<>("id"));
        appointmentTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentType.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStart.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        appointmentEnd.setCellValueFactory(new PropertyValueFactory<>("endDateTime"));
        appointmentCustomerID.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentContact.setCellValueFactory(new PropertyValueFactory<>("contactId"));
        appointmentUserID.setCellValueFactory(new PropertyValueFactory<>("userId"));
        // Populate table with data
        loadAllAppointments();

        initCustomerIDComboBox();
        initUserIDComboBox();
    }

    /**
     * Loads all appointments from the database and sets them to the appointments table.
     * <p>
     * This method fetches the complete list of appointments using the {@code AppointmentsDAO.getAllAppointments}
     * method and populates the {@code appointmentsTable} with the fetched data.
     * </p>
     */
    private void loadAllAppointments() {
        ObservableList<Appointments> appointmentData = AppointmentsDAO.getAllAppointments();
        appointmentsTable.setItems(appointmentData);
    }

    /**
     * Initializes the customerIDComboBox with Customer IDs fetched from the database.
     *
     * This method establishes a connection to the database using the {@link JDBC} helper class,
     * retrieves all Customer entities, extracts their Customer IDs, and populates the
     * customerIDComboBox with these IDs.
     *
     * If any SQL exceptions occur during the fetching process, an error message is logged
     * to the console, providing information on the exception encountered.
     */
    private void initCustomerIDComboBox() {
        try (Connection connection = JDBC.openConnection()) {
            ObservableList<Customers> allCustomers = CustomersDAO.retrieveAllCustomers(connection);
            ObservableList<Integer> customerIDs = FXCollections.observableArrayList();

            for (Customers customer : allCustomers) {
                customerIDs.add(customer.getCustomerId());
            }

            customerIDComboBox.setItems(customerIDs);

        } catch (SQLException e) {
            System.err.println("Error initializing Customer ID ComboBox: " + e.getMessage());
            // Handle the error as you see fit. Maybe display an error message to the user.
        }
    }

    /**
     * Initializes the userIDComboBox with User IDs fetched from the database.
     * This method attempts to retrieve all User entities from the database,
     * extract their User IDs, and populate the userIDComboBox with these IDs.
     * If there's an issue during this process, an error is logged to the console.
     */
    private void initUserIDComboBox() {
        try  {
            // I'm making a hypothetical assumption that you have a method like this
            ObservableList<User> allUsers = UserDAO.getAllUsers();
            ObservableList<Integer> userIDs = FXCollections.observableArrayList();

            for (User user : allUsers) {
                userIDs.add(user.getId());
            }

            userIDComboBox.setItems(userIDs);

        } catch (SQLException e) {
            System.err.println("Error initializing User ID ComboBox: " + e.getMessage());
        }
    }



    /**
     * Handle the action when the "Add Appointment" button is clicked.
     * This method utilizes the SceneChanger utility to navigate to the Add Appointment view.
     *
     * @param event The event triggered by clicking the "Add Appointment" button.
     */
    @FXML
    private void openAddAppointment(ActionEvent event) {
        System.out.println("openAddAppointment called!");
        try {
            SceneChanger.changeScene(event, "/view/AddAppointments.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Navigates the user to the main screen when invoked.
     * This method utilizes the SceneChanger utility to load and display the MainScreen view.
     *
     * @param event The event triggered by the UI control, usually a button click.
     */
    @FXML
    private void navigateToMain(ActionEvent event) {
        try {
            SceneChanger.changeScene(event, "/view/MainScreen.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action of removing the currently selected appointment from the table and database.
     *
     * <p>
     * This method retrieves the currently selected appointment from {@code appointmentsTable}.
     * If an appointment is selected, it prompts the user for deletion confirmation using
     * {@code showDeletionConfirmation}. If confirmed, the method attempts to delete the
     * appointment from the database using {@code AppointmentsDAO.deleteAppointment}. The table
     * is then refreshed using {@code refreshAppointmentTable}. If no appointment is selected,
     * it shows an error alert.
     * </p>
     *
     * @param event The action event that triggered this method. Typically a button click event.
     * @see AppointmentsDAO#deleteAppointment(int)
     * @see #showDeletionConfirmation(Appointments)
     * @see #refreshAppointmentTable()
     */
    @FXML
    void removeSelectedAppointment(ActionEvent event) {
        Appointments selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            if (showDeletionConfirmation(selectedAppointment)) {
                if (AppointmentsDAO.deleteAppointment(selectedAppointment.getId()) > 0) {  // Refactored this line
                    UIUtils.showInfoAlert("Appointment deleted successfully!");
                    refreshAppointmentTable();
                } else {
                    UIUtils.showErrorAlert("Failed to delete the appointment!");
                }
            }
        } else {
            UIUtils.showErrorAlert("No appointment selected!");
        }
    }

    /**
     * Presents a confirmation dialog to the user asking if they want to delete the specified appointment.
     *
     * <p>
     * This method displays a confirmation dialog with details of the appointment the user is
     * attempting to delete. The dialog provides a brief overview of the appointment using its
     * ID and type for clarity. The user can then confirm or cancel the deletion request.
     * </p>
     *
     * @param appointment The {@code Appointments} object representing the appointment that the
     *                    user wants to delete.
     * @return {@code true} if the user confirms the deletion; {@code false} otherwise.
     * @see Alert
     */
    private boolean showDeletionConfirmation(Appointments appointment) {
        String appointmentDetails = "Appointment ID: " + appointment.getId() + ", Type: " + appointment.getType();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete the following appointment?\n" + appointmentDetails);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Refreshes the appointment table with the latest data from the database.
     *
     * <p>
     * This method retrieves the current list of appointments from the database and updates the
     * appointment table view accordingly. It's typically called after any CRUD operations that
     * modify the database to ensure the table displays the most up-to-date information.
     * </p>
     *
     * @see AppointmentsDAO#getAllAppointments
     * @see TableView#setItems
     */
    private void refreshAppointmentTable() {
        ObservableList<Appointments> updatedAppointments = AppointmentsDAO.getAllAppointments();
        appointmentsTable.setItems(updatedAppointments);
    }

    /**
     * Loads the details of the selected appointment into the form fields.
     * <p>
     * This method establishes a database connection, retrieves the selected appointment from the table,
     * fetches all the contacts, and fills the form fields with the appointment details.
     * If any exception occurs during this process, an error alert is shown.
     * </p>
     */
    @FXML
    private void loadSelectedAppointment() {
        System.out.println("Entered loadSelectedAppointment()");  // Debug log

        Appointments selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment == null) {
            return;  // No appointment is selected. No need to proceed.
        }

        populateFormFields(selectedAppointment);

        // Only populate ComboBoxes if they are empty
        if(contactCombo.getItems().isEmpty()) {
            loadContactNamesToCombo(selectedAppointment.getContactId());
        }

        if(startTimeCombo.getItems().isEmpty()) {
            List<String> appointmentTimes = generateAppointmentTimes();
            startTimeCombo.setItems(FXCollections.observableArrayList(appointmentTimes));
            endTimeCombo.setItems(FXCollections.observableArrayList(appointmentTimes));
        }

        System.out.println("Exiting loadSelectedAppointment()");  // Debug log

    }

    /**
     * Populates the form fields based on the details of a selected appointment.
     *
     * <p>
     * This method updates various fields on the user interface form to reflect the details
     * of a given appointment. This is useful, for instance, when editing an existing appointment,
     * where the form needs to be pre-filled with the current details of the selected appointment.
     * </p>
     *
     * @param selectedAppointment The {@link Appointments} object containing details of the selected appointment.
     *
     * @see Appointments
     */
    private void populateFormFields(Appointments selectedAppointment) {
        appointmentIDField.setText(String.valueOf(selectedAppointment.getId()));
        titleField.setText(selectedAppointment.getTitle());
        descriptionField.setText(selectedAppointment.getDescription());
        locationField.setText(selectedAppointment.getLocation());
        typeField.setText(selectedAppointment.getType());
        customerIDComboBox.setValue(selectedAppointment.getCustomerId());
        startDatePicker.setValue(selectedAppointment.getStartDateTime().toLocalDate());
        endDatePicker.setValue(selectedAppointment.getEndDateTime().toLocalDate());
        startTimeCombo.setValue(selectedAppointment.getStartDateTime().toLocalTime().toString());
        endTimeCombo.setValue(selectedAppointment.getEndDateTime().toLocalTime().toString());
        userIDComboBox.setValue(selectedAppointment.getUserId());
    }

    /**
     * Loads contact names into the combo box and selects the contact with the specified ID.
     *
     * <p>
     * This method retrieves a list of all contacts from the database and populates
     * the contact combo box with their names. After populating the combo box, it sets
     * the selected value to the contact name that matches the provided selectedContactId.
     * This is particularly useful when the form needs to be pre-filled with the current
     * details of a contact, e.g., during an edit operation.
     * </p>
     *
     * <p>
     * Any exception encountered during the operation will be caught and the user is informed
     * via an error alert. Additionally, after the operation, the database connection is
     * closed to free up resources.
     * </p>
     *
     * @param selectedContactId The ID of the contact that needs to be selected in the combo box.
     *
     * @see Contacts
     * @see ContactsDAO
     * @see UIUtils
     * @see JDBC
     */
    private void loadContactNamesToCombo(int selectedContactId) {
        Connection conn = null;
        try {
            conn = JDBC.openConnection();

            List<Contacts> allContacts = ContactsDAO.getAllContacts();

            String selectedContactName = "";
            List<String> contactNames = new ArrayList<>();

            for (Contacts contact : allContacts) {
                contactNames.add(contact.getContactName());
                if (contact.getContactId() == selectedContactId) {
                    selectedContactName = contact.getContactName();
                }
            }

            contactCombo.setItems(FXCollections.observableArrayList(contactNames));
            contactCombo.setValue(selectedContactName);

        } catch (Exception e) {
            UIUtils.showErrorAlert("An error occurred while loading contact names.");
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    /**
     * Generate a list of times in 15-minute intervals for the ComboBoxes.
     */

    private List<String> generateAppointmentTimes() {
        if (times != null) {
            return times;
        }

        times = new ArrayList<>();
        LocalTime startTime = LocalTime.of(8, 0);  // Start from 8:00 AM
        LocalTime endTime = LocalTime.of(23, 45);  // End at 11:45 PM

        while (!startTime.isAfter(endTime)) {
            times.add(startTime.toString());
            startTime = startTime.plusMinutes(15);

            // Check to avoid infinite loop
            if (startTime.equals(endTime)) {
                break;
            }
        }

        return times;
    }

    /**
     * Determines if the given appointment time is within business hours and not during weekends.
     *
     * <p>
     * This method first converts the provided local date-time values for appointment start and end to Eastern Standard Time (EST).
     * It then checks if either of the converted date-time values fall on a weekend. Additionally, the method verifies if the provided
     * appointment times are outside of the standard business hours, which are from 8 am to 10 pm EST.
     * </p>
     *
     * <p>
     * If the provided appointment times violate any of the mentioned conditions, an appropriate error alert is shown to the user.
     * </p>
     *
     * @param startLocalDateTime The starting date and time of the appointment in the local timezone.
     * @param endLocalDateTime The ending date and time of the appointment in the local timezone.
     * @return {@code true} if the appointment time is within business hours and not during weekends; {@code false} otherwise.
     *
     * @see TimeConversionUtil
     * @see UIUtils
     */
    private boolean isTimeWithinBusinessHours(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        LocalDateTime startEST = TimeConversionUtil.convertLocalToEST(startLocalDateTime);
        LocalDateTime endEST = TimeConversionUtil.convertLocalToEST(endLocalDateTime);

        // Check if the appointment is during the weekend
        if (TimeConversionUtil.isWeekendInEST(startEST) || TimeConversionUtil.isWeekendInEST(endEST)) {
            UIUtils.showAlert("Appointment Error", "Appointments cannot be scheduled on weekends.");
            return false;
        }

        // Check if the appointment is outside of business hours (8 am - 10 pm EST)
        if (TimeConversionUtil.isOutsideBusinessHoursInEST(startEST) || TimeConversionUtil.isOutsideBusinessHoursInEST(endEST)) {
            UIUtils.showAlert("Appointment Error", "Appointments must be between 8 am and 10 pm EST.");
            return false;
        }

        return true;
    }

    /**
     * Validates the provided start and end times for an appointment.
     *
     * <p>
     * This method checks the following conditions:
     * 1. The start date-time should not be in the past.
     * 2. The end date-time should be after the start date-time.
     * 3. The start date-time and end date-time should not be the same.
     * </p>
     *
     * <p>
     * If any of the aforementioned conditions are violated, an appropriate error alert is displayed to the user,
     * and the method returns {@code false}. Otherwise, it returns {@code true}.
     * </p>
     *
     * @param startDate The starting date and time of the appointment.
     * @param endDate The ending date and time of the appointment.
     * @return {@code true} if the start and end date-times are valid; {@code false} otherwise.
     *
     * @see UIUtils
     */
    private boolean areStartAndEndTimesValid(LocalDateTime startDate, LocalDateTime endDate) {
        // Check if the start date is in the past
        if (startDate.isBefore(LocalDateTime.now())) {
            UIUtils.showAlert("Invalid Dates", "Appointments cannot be scheduled in the past.");
            return false;
        }
        if (endDate.isBefore(startDate)) {
            UIUtils.showAlert("Invalid Dates", "End Date & Time should be after Start Date & Time.");
            return false;
        } else if (endDate.equals(startDate)) {
            UIUtils.showAlert("Invalid Dates", "Start Date & Time should not be the same as End Date & Time.");
            return false;
        }
        return true;
    }




    /**
     * Validates all the input fields required for an appointment.
     *
     * <p>This method checks the following:</p>
     * <ul>
     *     <li>Whether essential text fields are non-empty.</li>
     *     <li>Correct date-time format for start and end times.</li>
     *     <li>End time should be after the start time.</li>
     *     <li>Both start and end times fall within the allowed business hours.</li>
     *     <li>Entered values for certain fields should be valid integers.</li>
     *     <li>Ensure a contact is selected.</li>
     * </ul>
     *
     * <p>Lambdas are utilized in this method to simplify repetitive validation checks. Specifically:</p>
     * <ul>
     *     <li>{@code validateEmpty} - Checks whether a TextField is empty or not.</li>
     *     <li>{@code validateDateTime} - Validates date and time fields to ensure they're in the correct format.</li>
     *     <li>{@code validateInteger} - Ensures a TextField contains a valid integer.</li>
     * </ul>
     *
     * @return {@code true} if all fields are valid; {@code false} otherwise.
     */
    private boolean validateFields() {
        final boolean[] areFieldsValid = {true};

        // Lambda to simplify field validation logic for checking emptiness
        Consumer<TextField> validateEmpty = field -> {
            if (field.getText().trim().isEmpty()) {
                field.setStyle("-fx-border-color: red;"); // Highlight in red
                areFieldsValid[0] = false;
            } else {
                field.setStyle(""); // Reset style
            }
        };

        // Lambda for date and time validation
        BiFunction<DatePicker, ComboBox<String>, LocalDateTime> validateDateTime = (datePicker, timeCombo) -> {
            LocalDate date = datePicker.getValue();
            String time = timeCombo.getSelectionModel().getSelectedItem();
            if (date == null || time == null || time.isEmpty()) {
                datePicker.setStyle("-fx-border-color: red;"); // Highlight in red
                timeCombo.setStyle("-fx-border-color: red;"); // Highlight in red
                areFieldsValid[0] = false;
                return null;
            } else {
                datePicker.setStyle(""); // Reset style
                timeCombo.setStyle(""); // Reset style
                return LocalDateTime.of(date, LocalTime.parse(time));
            }
        };

        // Using the lambdas for various text fields
        validateEmpty.accept(titleField);
        validateEmpty.accept(descriptionField);
        validateEmpty.accept(locationField);
        validateEmpty.accept(typeField);
        //validateEmpty.accept(appointmentIDField);

        // Using the lambda for start and end date and time fields
        LocalDateTime startDate = validateDateTime.apply(startDatePicker, startTimeCombo);
        LocalDateTime endDate = validateDateTime.apply(endDatePicker, endTimeCombo);

        // Ensure end date is after start date
        if (startDate != null && endDate != null) {
            if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
                startDatePicker.setStyle("-fx-border-color: red;"); // Highlight in red
                startTimeCombo.setStyle("-fx-border-color: red;"); // Highlight in red
                endDatePicker.setStyle("-fx-border-color: red;"); // Highlight in red
                endTimeCombo.setStyle("-fx-border-color: red;"); // Highlight in red
                UIUtils.showAlert("Invalid Dates", "End Date & Time should be after Start Date & Time.");
                areFieldsValid[0] = false;
            }
            // Validate start and end times
            if (!areStartAndEndTimesValid(startDate, endDate)) {
                areFieldsValid[0] = false;
            }
            // Ensure start and end times are within business hours
            if (!isTimeWithinBusinessHours(startDate, endDate)) {
                areFieldsValid[0] = false;
            }
        }

        // Lambda for ComboBox<Integer> validation
        Consumer<ComboBox<Integer>> validateIntegerComboBox = comboBox -> {
            Integer selectedValue = comboBox.getSelectionModel().getSelectedItem();
            if (selectedValue == null) {
                comboBox.setStyle("-fx-border-color: red;"); // Highlight in red
                areFieldsValid[0] = false;
            } else {
                comboBox.setStyle(""); // Reset style
            }
        };

        // Using the lambda for customer ID and user ID combo boxes
        validateIntegerComboBox.accept(customerIDComboBox);
        validateIntegerComboBox.accept(userIDComboBox);

        // Validate the contact ComboBox
        if (contactCombo.getSelectionModel().isEmpty()) {
            contactCombo.setStyle("-fx-border-color: red;"); // Highlight in red
            areFieldsValid[0] = false;
        } else {
            contactCombo.setStyle(""); // Reset style
        }

        return areFieldsValid[0];
    }

    /**
     * Inserts a new appointment into the database.
     *
     * <p>
     * This method calls the {@link AppointmentsDAO#insertAppointment} method to insert the given appointment into the database.
     * If the operation fails (e.g., due to a conflict, constraint violation, or any other reason that results in a non-positive
     * returned result), an error alert is shown to the user.
     * </p>
     *
     * @param appointment The {@link Appointments} object representing the new appointment to be inserted.
     *
     * @see AppointmentsDAO
     * @see UIUtils
     */
    private void insertNewAppointment(Appointments appointment) {
        int result = AppointmentsDAO.insertAppointment(appointment);

        if (result <= 0) {
            UIUtils.showErrorAlert("Failed to save new appointment.");
        }
    }

    /**
     * Handles the saving of appointment details when triggered.
     *
     * <p>
     * This method is responsible for:
     * 1. Validating the input fields related to the appointment.
     * 2. Checking for any overlapping appointments.
     * 3. Fetching the contact ID for the selected contact name.
     * 4. Either updating the existing appointment or inserting a new one based on the appointment's ID.
     * </p>
     *
     * <p>
     * Note: The logic assumes automatic conversion from local time to UTC, hence explicit UTC conversions are commented out.
     * </p>
     *
     * @param event The {@link ActionEvent} triggered by the user interaction, typically a button press to save appointment details.
     *
     * @throws SQLException If there's any database error while fetching the contact ID.
     *
     * @see AppointmentsDAO
     * @see TimeConversionUtil
     * @see UIUtils
     */
    @FXML
    void saveAppointmentDetails(ActionEvent event) {
        // 1. Validate Fields
        if (!validateFields()) {
            UIUtils.showErrorAlert("Invalid input fields.");
            return;
        }

        // 2. Fetch the date-time values without UTC conversion
        LocalDateTime startLocalDateTime = LocalDateTime.of(startDatePicker.getValue(), LocalTime.parse(startTimeCombo.getSelectionModel().getSelectedItem()));
        LocalDateTime endLocalDateTime = LocalDateTime.of(endDatePicker.getValue(), LocalTime.parse(endTimeCombo.getSelectionModel().getSelectedItem()));

        // The following UTC conversion is commented out because it's assumed to be happening automatically
        // String startUTCStr = TimeConversionUtil.convertLocalToUTC(startLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // String endUTCStr = TimeConversionUtil.convertLocalToUTC(endLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // LocalDateTime startUTC = LocalDateTime.parse(startUTCStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        // LocalDateTime endUTC = LocalDateTime.parse(endUTCStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // 3. Check for overlapping appointments
        int customerId = customerIDComboBox.getSelectionModel().getSelectedItem(); // Using ComboBox for customerID
        int appointmentId = appointmentIDField.getText().isEmpty() ? 0 : Integer.parseInt(appointmentIDField.getText()); // Assuming 0 indicates a new appointment
        int userId = userIDComboBox.getSelectionModel().getSelectedItem(); // Using ComboBox for userID

        if (AppointmentsDAO.hasOverlappingAppointments(startLocalDateTime, endLocalDateTime, customerId, appointmentId)) {
            UIUtils.showErrorAlert("The customer has another appointment in the chosen time slot. Please select a different time.");
            return;
        }

        String selectedContactName = contactCombo.getSelectionModel().getSelectedItem();
        int contactId = -1;  // Default value
        try {
            contactId = findContactID(selectedContactName);
            if (contactId == -1) { // No matching contact found
                UIUtils.showErrorAlert("Error fetching contact details. Please try again.");
                return;
            }
        } catch (SQLException e) {
            UIUtils.showErrorAlert("Database error: " + e.getMessage());
            return;
        }

        // 4. Update the appointment if it exists or insert a new one if it doesn't
        Appointments appointment = new Appointments(
                appointmentId,
                titleField.getText(),
                descriptionField.getText(),
                locationField.getText(),
                typeField.getText(),
                startLocalDateTime,  // Using local time here since the UTC conversion is assumed to be automatic
                endLocalDateTime,    // Using local time here as well
                customerId,
                contactId,
                userId
        );

        if (appointmentId == 0) {
            insertNewAppointment(appointment);
        } else {
            int rowsUpdated = AppointmentsDAO.updateAppointment(appointment);
            if (rowsUpdated <= 0) {
                // Handle no update case. Maybe the appointment doesn't exist or there was an SQL error.
                UIUtils.showErrorAlert("Error updating the appointment. Please try again.");
            } else {
                UIUtils.showInfoAlert("Appointment saved successfully.");
            }
        }

        refreshAppointmentTable(); // refresh the view.
    }


    /**
     * Fetches and displays all appointments in the appointmentsTable when the "All Appointments" RadioButton is selected.
     * <p>
     * This method retrieves all appointments using the AppointmentsDAO class. If no appointments
     * are found, it informs the user with a friendly message. In case of any exception during
     * the data fetch process, it provides a user-friendly error alert.
     * </p>
     *
     * @param event The action event triggered by the RadioButton.
     * @see AppointmentsDAO#getAllAppointments()
     * @see UIUtils#showInfoAlert(String)
     * @see UIUtils#showErrorAlert(String)
     */
    @FXML
    private void showAllAppointments(ActionEvent event) {
        try {
            ObservableList<Appointments> appointmentsList = AppointmentsDAO.getAllAppointments();

            if (appointmentsList.isEmpty()) {
                UIUtils.showInfoAlert("No appointments found.");
            } else {
                appointmentsTable.setItems(appointmentsList);
            }
        } catch (Exception e) {
            // Handle the exception, showing an alert to the user
            UIUtils.showErrorAlert("An error occurred while fetching appointments. Please try again.");
            e.printStackTrace();
        }
    }


    /**
     * Handles the RadioButton selection for viewing appointments of the current month.
     * <p>
     * This method fetches all the appointments and then filters them to show only the
     * appointments within the current month. If no monthly appointments are found, it
     * informs the user with a friendly message. For any exceptions during the data fetch
     * process, a user-friendly error alert is provided.
     * </p>
     *
     * @param event The action event triggered by the RadioButton.
     * @see AppointmentsDAO#getAllAppointments()
     * @see UIUtils#showInfoAlert(String)
     * @see UIUtils#showErrorAlert(String)
     */
    @FXML
    private void showMonthlyAppointments(ActionEvent event) {
        try {
            ObservableList<Appointments> allAppointments = AppointmentsDAO.getAllAppointments();
            ObservableList<Appointments> monthlyAppointments = FXCollections.observableArrayList();

            // Define the start and end times for the current month
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);

            for (Appointments appointment : allAppointments) {
                if (appointment.getEndDateTime().isAfter(startOfMonth) && appointment.getEndDateTime().isBefore(endOfMonth)) {
                    monthlyAppointments.add(appointment);
                }
            }

            if(monthlyAppointments.isEmpty()) {
                UIUtils.showInfoAlert("No appointments found for the current month.");
            } else {
                appointmentsTable.setItems(monthlyAppointments);
            }
        } catch (Exception e) {
            // Handle the exception and show an alert to the user
            UIUtils.showErrorAlert("An error occurred while fetching monthly appointments. Please try again.");
            e.printStackTrace();
        }
    }

    /**
     * Handles the action event when the weekly view RadioButton is selected.
     * It fetches all appointments from the database and filters out the ones that are
     * scheduled within the current week (from one week ago to one week in the future).
     * The filtered appointments are then displayed in the appointmentsTable.
     *
     * @param event the action event triggered by the RadioButton selection.
     */
    @FXML
    void showWeeklyAppointments(ActionEvent event) {
        try {
            ObservableList<Appointments> fetchedAppointments = AppointmentsDAO.getAllAppointments();

            ObservableList<Appointments> weeklyAppointments = FXCollections.observableArrayList();

            LocalDateTime currentDateTime = LocalDateTime.now();
            LocalDateTime startOfWeek = currentDateTime.minusWeeks(1);
            LocalDateTime endOfWeek = currentDateTime.plusWeeks(1);

            for (Appointments appointment : fetchedAppointments) {
                if (appointment.getEndDateTime().isAfter(startOfWeek) && appointment.getEndDateTime().isBefore(endOfWeek)) {
                    weeklyAppointments.add(appointment);
                }
            }

            if (!weeklyAppointments.isEmpty()) {
                appointmentsTable.setItems(weeklyAppointments);
            } else {
                UIUtils.showInfoAlert("No appointments found for the current week.");
            }

        } catch (Exception e) {
            UIUtils.showErrorAlert("An error occurred while fetching weekly appointments.");
            e.printStackTrace();
        }
    }




}
