package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Countries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides data access operations for the Countries entity.
 */
public class CountriesDAO {

    /**
     * Queries the countries table and retrieves a list of countries.
     *
     * @return ObservableList of countries from the database.
     * @throws SQLException if there's an error accessing the database.
     */
    public static ObservableList<Countries> retrieveAllCountries(Connection connection) throws SQLException {
        ObservableList<Countries> countriesList = FXCollections.observableArrayList();

        String sqlQuery = "SELECT Country_ID, Country from countries";

        // Ensure connection is not null
        if (connection == null) {
            throw new SQLException("The provided database connection is null.");
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int countryID = resultSet.getInt("Country_ID");
                String countryName = resultSet.getString("Country");
                Countries country = new Countries(countryID, countryName);
                countriesList.add(country);
            }
        }

        return countriesList;
    }

}
