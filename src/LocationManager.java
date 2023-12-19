import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationManager {

    private Connection connection;

    public LocationManager(Connection connection) {
        this.connection = connection;
    }

    public boolean saveOrUpdateLocation(Point location) {
        try {
            // Check if the location already exists
            int existingPointID = getExistingPointID(location);

            if (existingPointID == -1) {
                // Location does not exist, insert new location
                insertNewLocation(location);
            }
            // If the location already exists, do nothing
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to insert a new location
    private void insertNewLocation(Point location) throws SQLException {
        String insertQuery = "INSERT INTO PointTable (X, Y) VALUES (?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insertStatement.setInt(1, location.getX());
            insertStatement.setInt(2, location.getY());
            insertStatement.executeUpdate();

            // Get the generated PointID
            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int pointID = generatedKeys.getInt(1);

                    // Insert into LocationTable
                    String locationInsertQuery = "INSERT INTO LocationTable (PointID) VALUES (?)";
                    try (PreparedStatement locationInsertStatement = connection.prepareStatement(locationInsertQuery)) {
                        locationInsertStatement.setInt(1, pointID);
                        locationInsertStatement.executeUpdate();
                    }
                }
            }
        }
    }

    // Helper method to get PointID by coordinates
    private int getExistingPointID(Point location) throws SQLException {
        String query = "SELECT PointID FROM PointTable WHERE X = ? AND Y = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, location.getX());
            preparedStatement.setInt(2, location.getY());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("PointID") : -1;
            }
        }
    }
}
