package controller;

import helper.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointments;
import model.Countries;
import model.Customers;
import model.Division;
import utils.SceneChanger;
import utils.UIUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CustomerController implements Initializable {

    @FXML
    private TableView<Customers> tblCustomerRecords;
    @FXML
    private TableColumn<Customers, Integer> colCustomerId;
    @FXML
    private TableColumn<Customers, String> colCustomerName;
    @FXML
    private TableColumn<Customers, String> colCustomerAddress;
    @FXML
    private TableColumn<Customers, String> colPostalCode;
    @FXML
    private TableColumn<Customers, String> colPhoneNumber;
    @FXML
    private TableColumn<Customers, String> colFirstLevelData;
    @FXML
    private TextField txtCustomerId;
    @FXML
    private TextField txtCustomerName;
    @FXML
    private TextField txtCustomerAddress;
    @FXML
    private TextField txtPostalCode;
    @FXML
    private TextField txtPhoneNumber;
    @FXML
    private ComboBox<String> cmbCountry;
    @FXML
    private ComboBox<String> cmbFirstLevelData;


    @FXML
    private Button btnEdit;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnBack;

    /**
     * Initializes the controller after its root element has been completely processed.
     * This method fetches data from the database, configures the table view columns,
     * and populates the combo boxes.
     *
     * @param url The location used to resolve relative paths for the root object, or null if the location is not known.
     * @param resourceBundle The resources used to localize the root object, or null if the root object was not localized.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setupTableColumns();

            ObservableList<Countries> allCountries = fetchAllCountries();
            populateCountryComboBox(allCountries);

            ObservableList<Division> allFirstLevelDivisions = fetchAllFirstLevelDivisions();
            populateFirstLevelDataComboBox(allFirstLevelDivisions);

            ObservableList<Customers> allCustomersList = fetchAllCustomers();
            tblCustomerRecords.setItems(allCustomersList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the cell value factories for the table columns.
     */
    private void setupTableColumns() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colCustomerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCustomerAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        colPhoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colFirstLevelData.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
    }

    /**
     * Fetches all countries from the database.
     *
     * @return An observable list containing all countries.
     * @throws SQLException If there's an error during the database operation.
     */
    private ObservableList<Countries> fetchAllCountries() throws SQLException {
        Connection connection = JDBC.openConnection();
        if (connection == null) {
            throw new SQLException("Unable to establish a database connection.");
        }
        try {
            return CountriesDAO.retrieveAllCountries(connection);
        } finally {
            JDBC.closeConnection(connection);
        }
    }


    /**
     * Fetches all first level divisions from the database.
     *
     * @return An observable list containing all first level divisions.
     * @throws SQLException If there's an error during the database operation.
     */
    private ObservableList<Division> fetchAllFirstLevelDivisions() throws SQLException {
        Connection connection = null;
        try {
            connection = JDBC.openConnection();
            if (connection == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            return DivisionDAO.getAllDivisions(connection);
        } finally {
            if (connection != null) {
                JDBC.closeConnection(connection);
            }
        }
    }


    /**
     * Fetches all customers from the database.
     *
     * @return An observable list containing all customers.
     * @throws SQLException If there's an error during the database operation.
     */
    private ObservableList<Customers> fetchAllCustomers() throws SQLException {
        try (Connection connection = JDBC.openConnection()) {
            return CustomersDAO.retrieveAllCustomers(connection);
        }
    }

    /**
     * Populates the country combo box with country names.
     *
     * @param allCountries An observable list containing all countries.
     */
    private void populateCountryComboBox(ObservableList<Countries> allCountries) {
        ObservableList<String> countryNames = allCountries.stream()
                .map(Countries::getName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        cmbCountry.setItems(countryNames);
    }

    /**
     * Populates the first level data combo box with division names.
     *
     * @param allFirstLevelDivisions An observable list containing all first level divisions.
     */
    private void populateFirstLevelDataComboBox(ObservableList<Division> allFirstLevelDivisions) {
        ObservableList<String> firstLevelDivisionAllNames = allFirstLevelDivisions.stream()
                .map(Division::getName)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        cmbFirstLevelData.setItems(firstLevelDivisionAllNames);
    }

    /**
     * Handles the action when the 'Back' button is clicked.
     * This method utilizes the SceneChanger utility to transition back to the main screen.
     *
     * @param event The ActionEvent triggered by clicking the 'Back' button.
     */
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        SceneChanger.changeScene(event, "/view/MainScreen.fxml");
    }

    /**
     * Handles the action event when the delete customer button is pressed.
     * <p>
     * This method first confirms with the user if they really want to delete the selected customer
     * and all associated appointments. If the user confirms, it proceeds to delete the customer and their
     * associated appointments.
     *
     * @param event the event triggered when the 'delete-customer' button is pressed
     */
    @FXML
    void handleDeleteCustomer(ActionEvent event) {
        Customers selectedCustomer = tblCustomerRecords.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null) {
            UIUtils.showInfoAlert("Please select a customer to delete.");
            return;
        }

        Optional<ButtonType> confirmation = UIUtils.showConfirmationAlert("Confirmation", "Delete the selected customer and all appointments?");
        if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
            if (deleteCustomerAndAppointments(selectedCustomer.getCustomerId())) {
                UIUtils.showInfoAlert("Customer and their appointments deleted successfully.");
                refreshCustomerTable();
            } else {
                UIUtils.showErrorAlert("An error occurred while deleting the customer.");
            }
        }
    }

    /**
     * Deletes a specified customer and all their associated appointments from the database.
     *
     * @param customerId the ID of the customer to be deleted
     * @return true if the deletion was successful, false otherwise
     */
    private boolean deleteCustomerAndAppointments(int customerId) {
        Connection connection = null;  // Declare outside the try block
        try {
            connection = JDBC.openConnection();
            if (connection == null) {
                UIUtils.showErrorAlert("Failed to establish a database connection.");
                return false;
            }
            deleteCustomerAppointments(connection, customerId);
            deleteCustomer(connection, customerId);
        } catch (SQLException e) {
            UIUtils.showErrorAlert("SQL Exception: " + e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                JDBC.closeConnection(connection);  // Pass the connection object here
            }
        }
        return true;
    }



    /**
     * Deletes a specified customer from the database.
     *
     * @param connection the active database connection
     * @param customerId the ID of the customer to be deleted
     * @throws SQLException if any SQL error occurs during the operation
     */
    private void deleteCustomer(Connection connection, int customerId) throws SQLException {
        String sqlDelete = "DELETE FROM customers WHERE Customer_ID = ?";
        try (PreparedStatement psDelete = connection.prepareStatement(sqlDelete)) {
            psDelete.setInt(1, customerId);
            psDelete.execute();
        }
    }

    /**
     * Deletes all appointments associated with a specified customer from the database.
     *
     * @param connection the active database connection
     * @param customerId the ID of the customer whose appointments are to be deleted
     * @throws SQLException if any SQL error occurs during the operation
     */
    private void deleteCustomerAppointments(Connection connection, int customerId) throws SQLException {
        String sqlDeleteAppointments = "DELETE FROM appointments WHERE Customer_ID = ?";
        try (PreparedStatement psDeleteAppointments = connection.prepareStatement(sqlDeleteAppointments)) {
            psDeleteAppointments.setInt(1, customerId);
            psDeleteAppointments.execute();
        }
    }

    /**
     * Refreshes the customer table with the latest data from the database.
     *
     * This method retrieves all customers from the database and updates the customer table
     * to reflect any changes.
     */
    private void refreshCustomerTable() {
        Connection connection = null;  // Declare outside the try block
        try {
            connection = JDBC.openConnection();
            ObservableList<Customers> refreshCustomersList = CustomersDAO.retrieveAllCustomers(connection);
            tblCustomerRecords.setItems(refreshCustomersList);
        } catch (SQLException e) {
            UIUtils.showErrorAlert("SQL Exception: " + e.getMessage());
        } finally {
            JDBC.closeConnection(connection);  // Pass the connection object here
        }
    }

    /**
     * Handles the process of editing a selected customer.
     *
     * <p>
     * This method performs the following operations:
     * 1. Opens a connection to the database.
     * 2. Retrieves the selected customer from the table.
     * 3. Fetches lists of all available countries and divisions.
     * 4. Populates the form fields based on the details of the selected customer.
     * 5. Sets the appropriate division and country in the combo boxes based on the selected customer's details.
     * </p>
     *
     * <p>
     * Note: Lambda expressions are used in this method for concise and functional style filtering of streams.
     * </p>
     *
     * @param event The {@link ActionEvent} that gets triggered, typically by a user interaction like clicking an "Edit" button.
     *
     * @throws SQLException If any database operation fails.
     *
     * @see Customers
     * @see CountriesDAO
     * @see DivisionDAO
     * @see JDBC
     */
    @FXML
    private void handleEditCustomer(ActionEvent event) {
        Connection connection = null;
        try {
            connection = JDBC.openConnection();
            System.out.println("Connection successful!");

            Customers selectedCustomer = tblCustomerRecords.getSelectionModel().getSelectedItem();

            if (selectedCustomer != null) {
                ObservableList<Countries> allCountries = CountriesDAO.retrieveAllCountries(connection);
                ObservableList<Division> allDivisions = DivisionDAO.getAllDivisions(connection);

                System.out.println("Selected Customer Division ID: " + selectedCustomer.getDivisionId());

                txtCustomerId.setText(String.valueOf(selectedCustomer.getCustomerId()));
                txtCustomerName.setText(selectedCustomer.getName());
                txtCustomerAddress.setText(selectedCustomer.getAddress());
                txtPostalCode.setText(selectedCustomer.getPostalCode());
                txtPhoneNumber.setText(selectedCustomer.getPhoneNumber());

                Division selectedDivision = allDivisions.stream()
                        .filter(d -> d.getId() == selectedCustomer.getDivisionId())
                        .findFirst()
                        .orElse(null);

                if (selectedDivision != null) {
                    System.out.println("Matching Division Name from the list: " + selectedDivision.getName());
                    // Setting the combo box value on JavaFX Application Thread
                    Platform.runLater(() -> {
                        cmbFirstLevelData.setValue(selectedDivision.getName());
                    });

                    allCountries.stream()
                            .filter(c -> c.getId() == selectedDivision.getCountryId())
                            .findFirst().ifPresent(selectedCountry -> cmbCountry.setValue(selectedCountry.getName()));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBC.closeConnection(connection);
            System.out.println("Connection closed!");
        }
    }

    /**
     * Handles the addition of a new customer.
     *
     * <p>
     * This method undertakes several steps to add a customer:
     * 1. Opens a connection to the database.
     * 2. Validates if all input fields are filled.
     * 3. Fetches the next available customer ID.
     * 4. Inserts the customer's data into the database.
     * 5. Clears all input fields and refreshes the table view with the newly added customer.
     * </p>
     *
     * <p>
     * Note: No lambda expressions are directly used within this method. However, a reference to
     * other methods which might use lambda expressions is provided in the `@see` section.
     * </p>
     *
     * @param event The {@link ActionEvent} that is triggered, often by a user interaction like clicking an "Add" button.
     *
     * @throws SQLException If any database operation fails.
     *
     * @see JDBC
     * @see DivisionDAO
     * @see UIUtils
     */
    @FXML
    void handleAddCustomer(ActionEvent event) {
        Connection connection = null; // Declare the connection outside the try block for visibility in the finally block
        try {
            connection = JDBC.openConnection();
            if(connection == null) {
                throw new SQLException("Failed to open a connection to the database");
            }

            // Ensure none of the input fields are empty
            if (!txtCustomerName.getText().isEmpty() && !txtCustomerAddress.getText().isEmpty() && !txtPostalCode.getText().isEmpty() && !txtPhoneNumber.getText().isEmpty() && cmbCountry.getValue() != null && cmbFirstLevelData.getValue() != null) {

                int divisionId = 0;
                for (Division division : DivisionDAO.getAllDivisions(connection)) { // Pass the connection if required
                    if (cmbFirstLevelData.getSelectionModel().getSelectedItem().equals(division.getName())) {
                        divisionId = division.getId();
                    }
                }

                // Fetch the next valid Customer_ID
                String idQuery = "SELECT MAX(Customer_ID) as maxID FROM customers";
                int nextCustomerId;
                try (PreparedStatement ps = connection.prepareStatement(idQuery); ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nextCustomerId = rs.getInt("maxID") + 1; // Increment by one
                    } else {
                        throw new Exception("Failed to fetch the next customer ID.");
                    }
                }

                String insertStatement = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = connection.prepareStatement(insertStatement)) {
                    ps.setInt(1, nextCustomerId);
                    ps.setString(2, txtCustomerName.getText());
                    ps.setString(3, txtCustomerAddress.getText());
                    ps.setString(4, txtPostalCode.getText());
                    ps.setString(5, txtPhoneNumber.getText());
                    ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(7, "admin");
                    ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(9, "admin");
                    ps.setInt(10, divisionId);
                    ps.execute();
                }

                // Clear input fields
                txtCustomerId.clear();
                txtCustomerName.clear();
                txtCustomerAddress.clear();
                txtPostalCode.clear();
                txtPhoneNumber.clear();

                // Refresh the table with updated data
                refreshCustomerTable();
            } else {
                UIUtils.showInfoAlert("All fields must be filled in to add a customer.");
            }
        } catch (Exception e) {
            UIUtils.showErrorAlert("Error while adding customer: " + e.getMessage());
        } finally {
            JDBC.closeConnection(connection); // Always close the connection
        }
    }

    /**
     * Handles the saving of a customer's details.
     *
     * <p>
     * This method can both add a new customer or update an existing one. The logic distinguishes between the two
     * scenarios based on whether the customer ID field is empty. The steps involved are:
     * 1. Opens a connection to the database.
     * 2. Validates that all input fields are populated.
     * 3. Determines the appropriate division based on the selected division name.
     * 4. Executes either an insert (for new customers) or update (for existing customers) SQL operation.
     * 5. Clears all input fields and refreshes the table view to reflect the updated details.
     * </p>
     *
     * <p>
     * Note: No lambda expressions are directly used within this method. However, a reference to
     * other methods which might use lambda expressions is provided in the `@see` section.
     * </p>
     *
     * @param event The {@link ActionEvent} that is triggered, often by a user interaction like clicking a "Save" button.
     *
     * @throws SQLException If any database operation fails.
     *
     * @see JDBC
     * @see DivisionDAO
     * @see UIUtils
     */
    @FXML
    private void handleSaveCustomer(ActionEvent event) {
        Connection connection = null;
        try {
            // Open the connection using the JDBC class
            connection = JDBC.openConnection();
            if(connection == null) {
                throw new SQLException("Failed to open a connection to the database");
            }


            // Ensure none of the input fields are empty
            if (txtCustomerName.getText().isEmpty() || txtCustomerAddress.getText().isEmpty() || txtPostalCode.getText().isEmpty() || txtPhoneNumber.getText().isEmpty() || cmbCountry.getValue() == null || cmbFirstLevelData.getValue() == null) {
                UIUtils.showInfoAlert("All fields must be filled in to save a customer.");
                return;
            }

            int divisionId = 0;
            for (Division division : DivisionDAO.getAllDivisions(connection)) { // pass connection to the method
                if (cmbFirstLevelData.getSelectionModel().getSelectedItem().equals(division.getName())) {
                    divisionId = division.getId();
                    break;
                }
            }

            if (txtCustomerId.getText().isEmpty()) {
                // New customer (Insert)
                String insertStatement = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = connection.prepareStatement(insertStatement)) {
                    ps.setString(1, txtCustomerName.getText());
                    ps.setString(2, txtCustomerAddress.getText());
                    ps.setString(3, txtPostalCode.getText());
                    ps.setString(4, txtPhoneNumber.getText());
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(6, "admin"); // Consider using a dynamic value for createdBy
                    ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(8, "admin"); // Consider using a dynamic value for updatedBy
                    ps.setInt(9, divisionId);
                    ps.execute();
                }
            } else {
                // Existing customer (Update)
                String updateStatement = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
                try (PreparedStatement ps = connection.prepareStatement(updateStatement)) {
                    ps.setString(1, txtCustomerName.getText());
                    ps.setString(2, txtCustomerAddress.getText());
                    ps.setString(3, txtPostalCode.getText());
                    ps.setString(4, txtPhoneNumber.getText());
                    ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setString(6, "admin"); // Consider using a dynamic value for updatedBy
                    ps.setInt(7, divisionId);
                    ps.setInt(8, Integer.parseInt(txtCustomerId.getText()));
                    ps.execute();
                }
            }

            // Clear input fields
            txtCustomerId.clear();
            txtCustomerName.clear();
            txtCustomerAddress.clear();
            txtPostalCode.clear();
            txtPhoneNumber.clear();

            // Refresh the table with updated data
            refreshCustomerTable();

            UIUtils.showInfoAlert("Customer saved successfully.");

        } catch (Exception e) {
            UIUtils.showErrorAlert("Error while saving customer: " + e.getMessage());
        } finally {
            // Close the connection using the JDBC class
            JDBC.closeConnection(connection);
        }
    }


    /**
     * Handles the selection of a country from a combo box and updates the divisions combo box accordingly.
     *
     * <p>
     * This method executes the following steps:
     * 1. Opens a connection to the database.
     * 2. Maps predefined country names to their corresponding IDs.
     * 3. Identifies the selected country from the combo box and determines its ID.
     * 4. Filters the relevant divisions based on the selected country's ID.
     * 5. Populates the divisions combo box with the filtered divisions.
     * 6. Clears any pre-existing selection in the divisions combo box.
     * </p>
     *
     * @param event The {@link ActionEvent} that is triggered, often by a user interaction like selecting a country from a combo box.
     * @throws Exception If any operation (e.g., database, mapping) fails.
     * @see DivisionDAO#getAllDivisions(Connection) For retrieving all divisions from the database.
     * @see UIUtils For utility methods related to the UI.
     * @see java.util.stream.Stream For methods like filter() and map() used in this function.
     * @see Stream#map(Function) Used to map divisions to their names.
     * @lambda This method uses lambda expressions in the filtering and mapping of divisions.
     */
    @FXML
    private void handleCountrySelection(ActionEvent event) {
        Connection connection = null; // Declare the connection outside of the try block
        try {
            connection = JDBC.openConnection(); // Use the JDBC class to open the connection

            Map<String, Integer> countryNameToIdMap = new HashMap<>();
            countryNameToIdMap.put("U.S", 1);
            countryNameToIdMap.put("UK", 2);
            countryNameToIdMap.put("Canada", 3);

            // Get selected country name and its ID
            String selectedCountryName = cmbCountry.getSelectionModel().getSelectedItem();
            Integer selectedCountryId = countryNameToIdMap.get(selectedCountryName);

            // Return early if no country is selected or the country is not mapped
            if (selectedCountryName == null || selectedCountryId == null) return;

            // Filter relevant divisions
            List<Division> allDivisions = DivisionDAO.getAllDivisions(connection); // Pass the connection
            List<Division> relevantDivisions = allDivisions.stream()
                    .filter(division -> division.getCountryId() == selectedCountryId)
                    .toList();

            // Set the filtered divisions to the ComboBox
            ObservableList<String> divisionNames = relevantDivisions.stream()
                    .map(Division::getName)
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            cmbFirstLevelData.setItems(divisionNames);

            cmbFirstLevelData.getSelectionModel().clearSelection();

        } catch (Exception e) {
            UIUtils.showErrorAlert("Error while handling country selection: " + e.getMessage());
        } finally {
            JDBC.closeConnection(connection); // Always close the connection
        }
    }

}