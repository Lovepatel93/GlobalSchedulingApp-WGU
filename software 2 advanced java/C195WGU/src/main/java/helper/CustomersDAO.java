package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for the Customers entity. Provides CRUD operations related to Customers.
 */
public class CustomersDAO {

    /**
     * Fetches all customer details from the database.
     *
     * @param connection the database connection to be used for the query.
     * @return an ObservableList containing all customers from the database.
     * @throws SQLException in case of an error during the database operation.
     */
    public static ObservableList<Customers> retrieveAllCustomers(Connection connection) throws SQLException {
        ObservableList<Customers> allCustomersList = FXCollections.observableArrayList();

        // SQL statement to retrieve specific customer details for efficiency
        String sqlStatement = "SELECT c.Customer_ID, c.Customer_Name, c.Address, " +
                "c.Postal_Code, c.Phone, c.Division_ID, d.Division " +
                "FROM customers c INNER JOIN first_level_divisions d ON c.Division_ID = d.Division_ID";

        // Use try-with-resources to ensure resources are closed properly
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("Customer_ID");
                String name = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                int divId = resultSet.getInt("Division_ID");
                String divName = resultSet.getString("Division");

                Customers customer = new Customers(id, name, address, postalCode, phone, divId, divName);
                allCustomersList.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching customers: " + e.getMessage());
            throw e; // Rethrow to inform the caller about the failure
        }

        return allCustomersList;
    }
}
