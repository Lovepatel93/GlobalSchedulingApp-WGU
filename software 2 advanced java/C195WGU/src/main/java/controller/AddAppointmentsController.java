package controller;

import helper.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import model.Appointments;
import model.Contacts;
import model.Customers;
import model.User;
import utils.SceneChanger;
import utils.TimeConversionUtil;
import utils.UIUtils;

import java.time.DateTimeException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static helper.ContactsDAO.findContactID;

public class AddAppointmentsController {

    @FXML
    private TextField appointmentIDField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField locationField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private ComboBox<String> startTimeComboBox;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> endTimeComboBox;
    @FXML
    private ComboBox<String> contactComboBox;
    @FXML
    private ComboBox<Integer> customerIDComboBox;
    @FXML
    private ComboBox<Integer> userIDComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    @FXML
    public void initialize() throws SQLException {

        // Fetch all contacts and populate the contact ComboBox
        ObservableList<Contacts> contactsList = ContactsDAO.getAllContacts();
        ObservableList<String> contactNames = FXCollections.observableArrayList();

        // Use lambda to get all contact names from the list
        contactsList.forEach(contact -> contactNames.add(contact.getContactName()));

        // Set the items for the contact ComboBox
        contactComboBox.setItems(contactNames);

        // Populate the startTime and endTime ComboBoxes
        ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

        LocalTime firstAppointment = LocalTime.of(8, 0);  // start from 8:00
        LocalTime lastAppointment = LocalTime.of(22, 15); // up to 22:15

        // Populate every 15-minute interval between the two times
        while (firstAppointment.isBefore(lastAppointment)) {
            appointmentTimes.add(firstAppointment.format(DateTimeFormatter.ofPattern("HH:mm")));
            firstAppointment = firstAppointment.plusMinutes(15);
        }

        // Set the items for the time ComboBoxes
        startTimeComboBox.setItems(appointmentTimes);
        endTimeComboBox.setItems(appointmentTimes);

        initCustomerIDComboBox();
        initUserIDComboBox();
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
     * Checks if the given time range (start and end) falls within the business hours.
     * Business hours are considered to be between 8 am and 10 pm EST on weekdays.
     * Weekends (Saturday and Sunday) are not considered business days.
     *
     * @param startLocalDateTime Start time of the range as {@link LocalDateTime}
     * @param endLocalDateTime   End time of the range as {@link LocalDateTime}
     * @return {@code true} if the time range falls within business hours, {@code false} otherwise.
     *
     * @throws DateTimeException If the provided date-time objects are invalid.
     * @see LocalDateTime
     * @see TimeConversionUtil#convertLocalToEST(LocalDateTime)
     */
    private boolean isTimeWithinBusinessHours(LocalDateTime startLocalDateTime, LocalDateTime endLocalDateTime) {
        LocalDateTime startEST = TimeConversionUtil.convertLocalToEST(startLocalDateTime);
        LocalDateTime endEST = TimeConversionUtil.convertLocalToEST(endLocalDateTime);

        // Check if the appointment is during the weekend
        if (startEST.toLocalDate().getDayOfWeek().equals(DayOfWeek.SATURDAY) ||
                startEST.toLocalDate().getDayOfWeek().equals(DayOfWeek.SUNDAY) ||
                endEST.toLocalDate().getDayOfWeek().equals(DayOfWeek.SATURDAY) ||
                endEST.toLocalDate().getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            UIUtils.showAlert("Appointment Error", "Appointments cannot be scheduled on weekends.");
            return false;
        }

        // Check if the appointment is outside of business hours (8 am - 10 pm EST)
        if (startEST.toLocalTime().isBefore(LocalTime.of(8, 0)) ||
                startEST.toLocalTime().isAfter(LocalTime.of(22, 0)) ||
                endEST.toLocalTime().isBefore(LocalTime.of(8, 0)) ||
                endEST.toLocalTime().isAfter(LocalTime.of(22, 0))) {
            UIUtils.showAlert("Appointment Error", "Appointments must be between 8 am and 10 pm EST.");
            return false;
        }

        return true;
    }

    /**
     * Validates if the provided start and end dates are logically sequenced.
     * Specifically, this method checks if the end date is after the start date
     * and if the two dates are not the same.
     *
     * @param startDate The beginning of the date and time range.
     * @param endDate   The end of the date and time range.
     * @return <code>true</code> if the end date is strictly after the start date;
     *         <code>false</code> otherwise, while also displaying an alert to the user.
     */
    private boolean areStartAndEndTimesValid(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isBefore(startDate)) {
            UIUtils.showAlert("Invalid Dates", "End Date & Time should be before Start Date & Time.");
            return false;
        } else if (endDate.equals(startDate)) {
            UIUtils.showAlert("Invalid Dates", "Start Date & Time should not be the same as End Date & Time.");
            return false;
        }
        return true;
    }

    /**
     * Validates the form fields to ensure all necessary data is present and correctly formatted.
     * <p>
     * This method uses several lambda expressions to streamline the validation process:
     * </p>
     * <ul>
     *    <li>{@code validateEmpty} - checks if a given TextField is empty, and styles it red if it is.</li>
     *    <li>{@code validateDateTime} - validates both the date and time components ensuring neither is null or empty.</li>
     *    <li>{@code validateIntegerComboBox} - validates a ComboBox containing Integers, ensuring a value is selected.</li>
     * </ul>
     *
     * @return {@code true} if all fields are valid, {@code false} otherwise.
     *
     * @lambdaUsage {@code validateEmpty} - Simplifies the process of checking a TextField for emptiness
     * by abstracting the repetitive logic into a single expression. This reduces code redundancy and makes
     * the validation process more modular.
     *
     * @lambdaUsage {@code validateDateTime} - Merges date and time validation into a single lambda
     * for efficiency. This allows for a single check instead of separate checks for date and time,
     * ensuring both components are valid simultaneously.
     *
     * @lambdaUsage {@code validateIntegerComboBox} - Abstracts away the repetitive task of ensuring
     * an Integer value has been selected from a ComboBox. This simplifies ComboBox validation
     * by encapsulating the logic into a concise expression.
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
        LocalDateTime startDate = validateDateTime.apply(startDatePicker, startTimeComboBox);
        LocalDateTime endDate = validateDateTime.apply(endDatePicker, endTimeComboBox);

        // Ensure end date is after start date
        if (startDate != null && endDate != null) {
            if (!areStartAndEndTimesValid(startDate, endDate)) {
                areFieldsValid[0] = false;
            }
            if (!isTimeWithinBusinessHours(startDate, endDate)) {
                areFieldsValid[0] = false;
            }
            // Check if the appointment is in the past
            if (startDate.isBefore(LocalDateTime.now())) {
                UIUtils.showErrorAlert("Appointments cannot be scheduled in the past.");
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

        // Using the lambda for customer ID and user ID ComboBox
        validateIntegerComboBox.accept(customerIDComboBox);
        validateIntegerComboBox.accept(userIDComboBox);


        // Validate the contact ComboBox
        if (contactComboBox.getSelectionModel().isEmpty()) {
            contactComboBox.setStyle("-fx-border-color: red;"); // Highlight in red
            areFieldsValid[0] = false;
        } else {
            contactComboBox.setStyle(""); // Reset style
        }

        return areFieldsValid[0];
    }

    /**
     * Inserts a new appointment into the database using the provided Appointments object.
     *
     * <p>
     * This method attempts to add the given appointment to the database. If the insertion fails,
     * a user-friendly error alert is shown.
     * </p>
     *
     * @param appointment The {@code Appointments} object representing the appointment to be added.
     *                    This object should have all necessary data fields populated.
     *
     * @see AppointmentsDAO#insertAppointment
     */
    private void insertNewAppointment(Appointments appointment) {
        int result = AppointmentsDAO.insertAppointment(appointment);

        if (result <= 0) {
            UIUtils.showErrorAlert("Failed to save new appointment.");
        }
    }


    /**
     * Handles the save action when adding or updating an appointment.
     *
     * <p>
     * This method performs several key tasks in the following order:
     * <ol>
     *   <li>Validates all input fields.</li>
     *   <li>Retrieves start and end times directly from the date-picker and time combo-box.</li>
     *   <li>Checks if the chosen time slots overlap with existing appointments for the selected customer.</li>
     *   <li>Retrieves the contact ID using the selected contact name.</li>
     *   <li>Creates and inserts the new appointment into the database.</li>
     *   <li>Changes the scene back to the main appointment screen.</li>
     * </ol>
     * Error handling and alerts are also incorporated to provide appropriate feedback to the user.
     * </p>
     *
     * @param event The {@code ActionEvent} object representing the triggering action, usually the pressing of the save button.
     *
     * @see #validateFields
     * @see AppointmentsDAO#hasOverlappingAppointments
     * @see #insertNewAppointment
     * @see UIUtils#showErrorAlert
     * @see UIUtils#showInfoAlert
     * @see SceneChanger#changeScene
     */
    @FXML
    public void handleSaveAction(ActionEvent event) {
        try {
            // 1. Validate Fields
            if (!validateFields()) {
                return; // The validateFields method already handles error messages
            }

            // 2. Get LocalDateTime directly from date-picker and time combo-box
            LocalDateTime startLocalDateTime = LocalDateTime.of(startDatePicker.getValue(), LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem()));
            LocalDateTime endLocalDateTime = LocalDateTime.of(endDatePicker.getValue(), LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem()));

            // 3. Check for overlapping appointments
            int customerId = customerIDComboBox.getSelectionModel().getSelectedItem(); // Using ComboBox for customerID
            int userId = userIDComboBox.getSelectionModel().getSelectedItem(); // Using ComboBox for userID

            int nextAppointmentId;
            // Fetch the next valid Appointment_ID
            String idQuery = "SELECT MAX(Appointment_ID) as maxID FROM appointments";

            try (Connection connection = JDBC.openConnection()) {
                if (connection == null) {
                    throw new SQLException("Failed to establish a connection.");
                }

                try (PreparedStatement ps = connection.prepareStatement(idQuery);
                     ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        nextAppointmentId = rs.getInt("maxID") + 1; // Increment by one
                    } else {
                        throw new Exception("Failed to fetch the next appointment ID.");
                    }
                }
            }

            if (AppointmentsDAO.hasOverlappingAppointments(startLocalDateTime, endLocalDateTime, customerId, nextAppointmentId)) {
                UIUtils.showErrorAlert("The customer has another appointment in the chosen time slot. Please select a different time.");
                return;
            }

            // 4. Get Contact ID from Contact Name
            String selectedContactName = contactComboBox.getSelectionModel().getSelectedItem();
            int contactId = findContactID(selectedContactName);
            if (contactId == -1) { // No matching contact found
                UIUtils.showErrorAlert("Error fetching contact details. Please try again.");
                return;
            }

            // 5. Insert new appointment
            Appointments appointment = new Appointments(
                    nextAppointmentId,
                    titleField.getText(),
                    descriptionField.getText(),
                    locationField.getText(),
                    typeField.getText(),
                    startLocalDateTime,
                    endLocalDateTime,
                    customerId,
                    contactId,
                    userId
            );

            insertNewAppointment(appointment);
            UIUtils.showInfoAlert("Appointment added successfully.");

            // Change scene
            SceneChanger.changeScene(event, "/view/AppointmentScreen.fxml");
        } catch (IOException e) {
            UIUtils.showErrorAlert("Failed to return to the main appointment screen. Please try again or restart the application.");
        } catch (Exception ex) {
            UIUtils.showErrorAlert("Error: " + ex.getMessage());
            ex.printStackTrace(); // For debugging
        }
    }

    /**
     * Handles the cancel action by navigating back to the main appointment screen.
     * <p>
     * This method is triggered when the user presses a cancel button or similar UI component
     * to abort the current action and return to the previous screen.
     * </p>
     *
     * @param event The action event that initiated this method call, typically from a button press.
     * @throws IOException If there's an error navigating to the main appointment screen.
     */
    @FXML
    public void handleCancelAction(ActionEvent event) {
        try {
            SceneChanger.changeScene(event, "/view/AppointmentScreen.fxml");
        } catch (IOException e) {
            UIUtils.showErrorAlert("Failed to return to the main appointment screen. Please try again or restart the application.");
        }
    }


}
