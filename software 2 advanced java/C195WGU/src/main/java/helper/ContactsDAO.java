package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Contacts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for managing and querying contacts from the database.
 * Provides functionality to retrieve contacts and find contact IDs based on their names.
 */
public class ContactsDAO {

    /**
     * Retrieves all contacts from the database and populates them into an observable list.
     *
     * @return An observable list containing all contacts.
     * @throws SQLException if there is an issue fetching the data from the database.
     */
    public static ObservableList<Contacts> getAllContacts() throws SQLException {
        ObservableList<Contacts> contactsList = FXCollections.observableArrayList();
        String query = "SELECT * from contacts";

        try (
                Connection connection = JDBC.openConnection();
        ) {
            if (connection == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            try (
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    ResultSet resultSet = preparedStatement.executeQuery()
            ) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("Contact_ID");
                    String name = resultSet.getString("Contact_Name");
                    String email = resultSet.getString("Email");

                    Contacts contact = new Contacts(id, name, email);
                    contactsList.add(contact);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in getAllContacts: " + e.getMessage());
            throw e;  // Re-throwing the exception for the caller to handle.
        }

        return contactsList;
    }

    /**
     * Finds the contact ID given its name.
     *
     * @param contactName The name of the contact.
     * @return The ID of the contact or -1 if no matching contact was found.
     * @throws SQLException if there's an issue fetching the data from the database.
     */
    public static int findContactID(String contactName) throws SQLException {
        System.out.println("Finding ID for contact: " + contactName); //debug

        String query = "SELECT * FROM contacts WHERE Contact_Name = ?";
        int contactID = -1;

        try (
                Connection connection = JDBC.openConnection();
        ) {
            if (connection == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            try (
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
            ) {
                preparedStatement.setString(1, contactName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        contactID = resultSet.getInt("Contact_ID");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in findContactID: " + e.getMessage());
            throw e;  // Re-throwing the exception for the caller to handle.
        }

        System.out.println("Resolved ID for " + contactName + " is: " + contactID); // debug

        return contactID;  // Return -1 or any suitable default indicating no matching contact was found.
    }
}
