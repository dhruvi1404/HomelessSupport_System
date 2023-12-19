import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OccupancyManager {
    private Connection connection;

    public OccupancyManager(Connection connection) {
        this.connection = connection;
    }

    public boolean declareShelterOccupancy(String shelterName, String date, int occupancy) {
        try {
            int shelterId = getShelterIdByName(shelterName);

            if (shelterId > 0) {
                // Check if occupancy data already exists for the given shelter and date
                if (checkOccupancyData(shelterId, date, occupancy)) {
                    System.out.println("Occupancy data already exists. Updating...");

                    // If data exists, update the existing record
                    return updateOccupancyData(shelterId, date, occupancy);
                } else {
                    // If data doesn't exist, insert a new record
                    return insertOccupancyData(shelterId, date, occupancy);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private int getShelterIdByName(String shelterName) throws SQLException {
        String query = "SELECT ShelterID FROM Shelter WHERE Name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, shelterName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("ShelterID") : -1;
            }
        }
    }

    private boolean checkOccupancyData(int shelterId, String date, int occupancy) throws SQLException {
        String query = "SELECT * FROM Occupancy WHERE ShelterID = ? AND Date = ? AND Occupancy = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, shelterId);
            preparedStatement.setString(2, date);
            preparedStatement.setInt(3, occupancy);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if the data already exists
            }
        }
    }

    private boolean insertOccupancyData(int shelterId, String date, int occupancy) throws SQLException {
        String query = "INSERT INTO Occupancy (ShelterID, Date, Occupancy) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, shelterId);
            preparedStatement.setString(2, date);
            preparedStatement.setInt(3, occupancy);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    private boolean updateOccupancyData(int shelterId, String date, int occupancy) throws SQLException {
        String query = "UPDATE Occupancy SET Occupancy = ? WHERE ShelterID = ? AND Date = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, occupancy);
            preparedStatement.setInt(2, shelterId);
            preparedStatement.setString(3, date);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
