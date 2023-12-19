import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Service {

    private Connection connection; // Add this field

    public Service(Connection connection) {
        this.connection = connection;
    }

    public boolean defineService(String serviceName, int inspectionFrequency) {
        if (!isValidInput(serviceName, inspectionFrequency)) {
            return false;
        }

        PreparedStatement preparedStatement = null;

        try {
            // Check if the service already exists in the database
            if (serviceExists(serviceName)) {
                // Update the inspection frequency for an existing service
                updateService(serviceName, inspectionFrequency);
            } else {
                // Insert a new service into the database
                insertService(serviceName, inspectionFrequency);
            }

            return true; // Successfully defined the service

        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Error occurred during database operation
        } finally {
            // Close resources
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isValidInput(String serviceName, int inspectionFrequency) {
        return serviceName != null && !serviceName.isEmpty() && inspectionFrequency >= 0;
    }

    private boolean serviceExists(String serviceName) throws SQLException {
        String query = "SELECT COUNT(*) FROM Service WHERE ServiceName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, serviceName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private void updateService(String serviceName, int inspectionFrequency) throws SQLException {
        String query = "UPDATE Service SET InspectionFrequency = ? WHERE ServiceName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, inspectionFrequency);
            preparedStatement.setString(2, serviceName);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Service update failed. Service not found: " + serviceName);
            }
        }
    }

    private void insertService(String serviceName, int inspectionFrequency) throws SQLException {
        String query = "INSERT INTO Service (ServiceName, InspectionFrequency) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, serviceName);
            preparedStatement.setInt(2, inspectionFrequency);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Service insertion failed.");
            }
        }
    }
}
