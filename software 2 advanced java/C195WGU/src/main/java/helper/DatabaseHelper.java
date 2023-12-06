package helper;

import utils.LogUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class DatabaseHelper {

    // Perform login validation
    public static boolean validateLogin(String username, String password) {
        boolean isSuccess = false;
        String query = "SELECT COUNT(*) FROM client_schedule.users WHERE User_Name = ? AND Password = ?";

        try (Connection connection = JDBC.openConnection()) {

            // Check if the connection is not null
            if (connection == null) {
                System.out.println("Error: Unable to establish a connection to the database.");
                return false; // Exit early
            }

            // If connection is not null, continue with statement preparation and execution
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    resultSet.next();
                    int count = resultSet.getInt(1);
                    isSuccess = count > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            // Log the user activity regardless of the result
            LogUtils.logLoginActivity(username, isSuccess);
        }

        return isSuccess;
    }


}
