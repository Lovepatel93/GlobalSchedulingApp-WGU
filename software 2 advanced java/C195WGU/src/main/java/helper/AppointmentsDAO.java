package helper;

import model.Appointments;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.TimeConversionUtil;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object (DAO) for managing and querying appointments from the database.
 * Provides CRUD operations for appointments and specific functionalities like checking overlapping appointments.
 */
public class AppointmentsDAO {

    /**
     * Retrieves all appointments from the database and populates them into an observable list.
     *
     * @return An observable list containing all appointments.
     */
    public static ObservableList<Appointments> getAllAppointments() {
        ObservableList<Appointments> appointmentsList = FXCollections.observableArrayList();
        String sql = "SELECT * from appointments";

        try (Connection connection = JDBC.openConnection()) {
            if (connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                // Extracting and constructing appointment objects
                int id = resultSet.getInt("Appointment_ID");
                String title = resultSet.getString("Title");
                String description = resultSet.getString("Description");
                String location = resultSet.getString("Location");
                String type = resultSet.getString("Type");

                // Assume the times are already in local format.
                LocalDateTime localStartTime = resultSet.getTimestamp("Start").toLocalDateTime();
                LocalDateTime localEndTime = resultSet.getTimestamp("End").toLocalDateTime();

                // The conversion lines are commented out, as you're directly using the local times now.
                // String startTimeUTC = resultSet.getTimestamp("Start").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                // String localStartTimeString = TimeConversionUtil.convertUTCToLocal(startTimeUTC);
                // LocalDateTime localStartTime = LocalDateTime.parse(localStartTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                // String endTimeUTC = resultSet.getTimestamp("End").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                // String localEndTimeString = TimeConversionUtil.convertUTCToLocal(endTimeUTC);
                // LocalDateTime localEndTime = LocalDateTime.parse(localEndTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                int customerId = resultSet.getInt("Customer_ID");
                int userId = resultSet.getInt("User_ID");
                int contactId = resultSet.getInt("Contact_ID");

                Appointments appointment = new Appointments(id, title, description, location, type, localStartTime, localEndTime, customerId, contactId, userId);
                appointmentsList.add(appointment);
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception while fetching all appointments: " + e.getMessage());
        }

        // Print the size of the appointments list
        System.out.println("Number of appointments fetched: " + appointmentsList.size());

        return appointmentsList;
    }



    /**
     * Deletes an appointment based on its ID.
     *
     * @param appointmentId The ID of the appointment to be deleted.
     * @return The number of rows affected by the delete operation.
     */
    public static int deleteAppointment(int appointmentId) {
        int result = 0;
        String query = "DELETE FROM appointments WHERE Appointment_ID=?";

        try (Connection connection = JDBC.openConnection()) {
            if (connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, appointmentId);
            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL Exception while deleting an appointment: " + e.getMessage());
        }

        return result;
    }

    // ... [previous methods here] ...

    /**
     * Checks for overlapping appointments based on provided parameters.
     *
     * @param newStartDateTime Start time of the new appointment.
     * @param newEndDateTime End time of the new appointment.
     * @param customerId Customer ID to check for overlaps.
     * @param appointmentIdToExclude Appointment ID to exclude from the overlap check.
     * @return True if there are overlapping appointments, False otherwise.
     */
    public static boolean hasOverlappingAppointments(LocalDateTime newStartDateTime, LocalDateTime newEndDateTime, int customerId, int appointmentIdToExclude) {
        String query = "SELECT * FROM appointments WHERE Customer_ID = ? AND Appointment_ID != ? AND ((? BETWEEN Start AND End) OR (? BETWEEN Start AND End))";

        try (Connection connection = JDBC.openConnection()) {
            if (connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, customerId);
            preparedStatement.setInt(2, appointmentIdToExclude);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(newStartDateTime));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(newEndDateTime));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception while checking for overlapping appointments: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates an existing appointment in the database.
     *
     * @param appointment The appointment object with updated details.
     * @return The number of rows affected by the update operation.
     */
    public static int updateAppointment(Appointments appointment) {
        int result = 0;
        String query = "UPDATE appointments SET Title=?, Description=?, Location=?, Type=?, Start=?, End=?, Customer_ID=?, User_ID=?, Contact_ID=? WHERE Appointment_ID=?";

        try (Connection connection = JDBC.openConnection()) {
            if (connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // setting parameters for prepared statement based on the appointment object
            preparedStatement.setString(1, appointment.getTitle());
            preparedStatement.setString(2, appointment.getDescription());
            preparedStatement.setString(3, appointment.getLocation());
            preparedStatement.setString(4, appointment.getType());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(appointment.getStartDateTime()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(appointment.getEndDateTime()));
            preparedStatement.setInt(7, appointment.getCustomerId());
            preparedStatement.setInt(8, appointment.getUserId());
            preparedStatement.setInt(9, appointment.getContactId());
            preparedStatement.setInt(10, appointment.getId());

            // Logging the query just before execution
            System.out.println("Executing update: " + preparedStatement.toString());

            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL Exception while updating an appointment: " + e.getMessage());
            e.printStackTrace(); // Print the full stack trace to get more details on where and why the exception occurred
        }

        return result;
    }


    /**
     * Inserts a new appointment into the database.
     *
     * @param appointment The appointment object to be inserted.
     * @return The number of rows affected by the insert operation.
     */
    public static int insertAppointment(Appointments appointment) {
        int result = 0;
        String query = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = JDBC.openConnection()) {
            if (connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Convert the UTC LocalDateTime to Timestamp directly since the times are already in UTC
            Timestamp startTimestamp = Timestamp.valueOf(appointment.getStartDateTime());
            Timestamp endTimestamp = Timestamp.valueOf(appointment.getEndDateTime());

            // setting parameters for prepared statement based on the appointment object
            preparedStatement.setString(1, appointment.getTitle());
            preparedStatement.setString(2, appointment.getDescription());
            preparedStatement.setString(3, appointment.getLocation());
            preparedStatement.setString(4, appointment.getType());
            preparedStatement.setTimestamp(5, startTimestamp);
            preparedStatement.setTimestamp(6, endTimestamp);
            preparedStatement.setInt(7, appointment.getCustomerId());
            preparedStatement.setInt(8, appointment.getUserId());
            preparedStatement.setInt(9, appointment.getContactId());

            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("SQL Exception while inserting a new appointment: " + e.getMessage());
        }

        return result;
    }



    /**
     * Retrieves appointments from the database that are within the next 15 minutes of the current local time.
     *
     * @return An observable list containing the upcoming appointments.
     */
    public static ObservableList<Appointments> getUpcomingAppointmentsWithin15Minutes() {
        System.out.println("System Default Time Zone: " + ZoneId.systemDefault());

        System.out.println("Machine's Current time is: " + LocalDateTime.now());
        // Commented out the conversion demonstration
        // String localTimeStr = "2023-08-17 16:15:00";
        // String convertedUtc = TimeConversionUtil.convertLocalToUTC(localTimeStr);
        // System.out.println("Local Time: " + localTimeStr);
        // System.out.println("Converted UTC: " + convertedUtc);

        ObservableList<Appointments> upcomingAppointments = FXCollections.observableArrayList();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fifteenMinutesFromNow = now.plusMinutes(15);

        String sql = "SELECT * FROM appointments WHERE Start BETWEEN ? AND ?";

        try (Connection connection = JDBC.openConnection()) {
            if (connection == null) {
                throw new SQLException("Unable to establish a connection to the database.");
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(now));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(fifteenMinutesFromNow));
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("Appointment_ID");
                String title = resultSet.getString("Title");
                String description = resultSet.getString("Description");
                String location = resultSet.getString("Location");
                String type = resultSet.getString("Type");

                // Directly fetching local time without conversion
                LocalDateTime localStartTime = resultSet.getTimestamp("Start").toLocalDateTime();
                LocalDateTime localEndTime = resultSet.getTimestamp("End").toLocalDateTime();

                int customerId = resultSet.getInt("Customer_ID");
                int userId = resultSet.getInt("User_ID");
                int contactId = resultSet.getInt("Contact_ID");

                Appointments appointment = new Appointments(id, title, description, location, type, localStartTime, localEndTime, customerId, contactId, userId);
                upcomingAppointments.add(appointment);
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception while fetching upcoming appointments: " + e.getMessage());
        }

        return upcomingAppointments;
    }


}


