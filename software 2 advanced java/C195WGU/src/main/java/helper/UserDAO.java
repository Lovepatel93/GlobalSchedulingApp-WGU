package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import utils.LogUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Validates the provided username and password against the database.
     *
     * @param username The username to validate.
     * @param password The password to validate.
     * @return True if the credentials match a user in the database, otherwise false.
     */
    public static boolean validateLogin(String username, String password) {
        boolean isValid = false;
        Connection connection = null; // Declare outside the try-block for visibility in finally block

        try {
            connection = JDBC.openConnection();

            if(connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM client_schedule.users WHERE User_Name = ? AND Password = ?")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    int count = resultSet.getInt(1);
                    isValid = count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        } finally {
            // Log the user activity regardless of the result
            LogUtils.logLoginActivity(username, isValid);

            // Close the connection here
            if(connection != null) {
                JDBC.closeConnection(connection);
            }
        }
        return isValid;
    }


    /**
     * Retrieves all users from the database.
     *
     * @return ObservableList of User objects.
     * @throws SQLException
     */
    public static ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> usersObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from users";
        Connection connection = null; // Declare outside the try-block for visibility in finally block

        try {
            connection = JDBC.openConnection();

            if(connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int userId = resultSet.getInt("User_ID");
                    String userName = resultSet.getString("User_Name");
                    String userPassword = resultSet.getString("Password");
                    User user = new User(userId, userName, userPassword);
                    usersObservableList.add(user);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in getAllUsers: " + e.getMessage());
        } finally {
            // Close the connection here too
            if(connection != null) {
                JDBC.closeConnection(connection);
            }
        }
        return usersObservableList;
    }

}
