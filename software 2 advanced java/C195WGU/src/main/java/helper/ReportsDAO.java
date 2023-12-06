package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CountryReport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for report-related functions.
 * This class handles CRUD operations related to generating various reports.
 */
public class ReportsDAO {

    /**
     * Fetches a summary report of countries with their associated counts from the database.
     *
     * @return An observable list of CountryReport objects.
     * @throws SQLException If there's an error during the database operation.
     */
    public static ObservableList<CountryReport> fetchCountrySummary() throws SQLException {
        ObservableList<CountryReport> countryReports = FXCollections.observableArrayList();

        String sql = "SELECT countries.Country, COUNT(*) as countryCount " +
                "FROM customers " +
                "INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID " +
                "INNER JOIN countries ON countries.Country_ID = first_level_divisions.Country_ID " +
                "GROUP BY first_level_divisions.Country_ID " +
                "ORDER BY COUNT(*) DESC";

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = JDBC.openConnection();

            if(connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }

            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String countryName = resultSet.getString("Country");
                int count = resultSet.getInt("countryCount");

                CountryReport report = new CountryReport(countryName, count);
                countryReports.add(report);
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in fetchCountrySummary: " + e.getMessage());
            throw e; // Re-throwing the exception to alert the caller.
        } finally {
            // Close resultSet, preparedStatement, and connection in a safe manner.
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) JDBC.closeConnection(connection);
            } catch (SQLException se) {
                System.out.println("Error closing resources in fetchCountrySummary: " + se.getMessage());
            }
        }

        return countryReports;
    }

}
