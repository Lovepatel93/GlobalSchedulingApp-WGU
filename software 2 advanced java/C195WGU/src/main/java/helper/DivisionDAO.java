package helper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Division;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object (DAO) for the Division entity.
 * This class handles CRUD operations related to the Division model.
 */
public class DivisionDAO {

    /**
     * Fetches all divisions from the database and returns them as an observable list.
     *
     * @return An observable list containing all divisions.
     * @throws SQLException If there's an error during the database operation.
     */
    public static ObservableList<Division> getAllDivisions(Connection connection) throws SQLException {
        ObservableList<Division> divisionList = FXCollections.observableArrayList();

        String sql = "SELECT * FROM first_level_divisions";
        // Use a try-with-resources block to manage resources.
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int divisionId = resultSet.getInt("Division_ID");
                String divisionName = resultSet.getString("Division");
                int countryId = resultSet.getInt("COUNTRY_ID");

                Division division = new Division(divisionId, divisionName, countryId);
                divisionList.add(division);
            }
        } // The preparedStatement and resultSet will be closed automatically when exiting this block.

        return divisionList;
    }

}
