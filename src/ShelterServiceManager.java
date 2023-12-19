import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShelterServiceManager {
    private Connection connection;

    public ShelterServiceManager(Connection connection) {
        this.connection = connection;
    }

    public boolean addServiceToShelter(String shelterName, String serviceName) {
        if (!isValidInput(shelterName, serviceName)) {
            return false;
        }

        try {
            int shelterId = getShelterIdByName(shelterName);
            int serviceId = getServiceIdByName(serviceName);

            if (shelterId > 0 && serviceId > 0) {
                // Check if the entry already exists
                if (checkServiceForShelterExists(shelterId, serviceId)) {
                    //System.out.println("Service for Shelter already exists.");
                    return true;
                }

                // If the entry doesn't exist, add it
                return addServiceForShelter(shelterId, serviceId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isValidInput(String shelterName, String serviceName) {
        return shelterName != null && !shelterName.isEmpty() &&
                serviceName != null && !serviceName.isEmpty();
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

    private int getServiceIdByName(String serviceName) throws SQLException {
        String query = "SELECT ServiceID FROM Service WHERE ServiceName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, serviceName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("ServiceID") : -1;
            }
        }
    }

    private boolean checkServiceForShelterExists(int shelterId, int serviceId) throws SQLException {
        String query = "SELECT * FROM ShelterService WHERE ShelterID = ? AND ServiceID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, shelterId);
            preparedStatement.setInt(2, serviceId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next(); // Returns true if the entry exists
            }
        }
    }

    private boolean addServiceForShelter(int shelterId, int serviceId) throws SQLException {
        String query = "INSERT INTO ShelterService (ShelterID, ServiceID) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, shelterId);
            preparedStatement.setInt(2, serviceId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        }
    }
}
