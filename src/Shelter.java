import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Shelter {
    private int shelterID;
    private String name;
    private Point location;
    private int maxCapacity;

    public Shelter(int shelterID, String name, Point location, int maxCapacity) {
        this.shelterID = shelterID;
        this.name = name;
        this.location = location;
        this.maxCapacity = maxCapacity;
    }

    public int getShelterID() {
        return shelterID;
    }

    public String getName() {
        return name;
    }

    public Point getLocation() {
        return location;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public boolean isValid() {
        return shelterID > 0 && name != null && !name.isEmpty() &&
                location != null && Point.isValidCoordinate(location.getX()) && Point.isValidCoordinate(location.getY()) &&
                maxCapacity >= 0;
    }

    public boolean saveOrUpdate(Connection connection) {
        if (!isValid() || connection == null) {
            return false;
        }

        try {
            if (shelterExists(connection)) {
                updateShelter(connection);
            } else {
                insertShelter(connection);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean shelterExists(Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM Shelter WHERE ShelterID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, shelterID);
            try (var resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private void updateShelter(Connection connection) throws SQLException {
        String query = "UPDATE Shelter SET Name = ?, Location = POINT(?, ?), MaxCapacity = ? WHERE ShelterID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, location.getX());
            preparedStatement.setInt(3, location.getY());
            preparedStatement.setInt(4, maxCapacity);
            preparedStatement.setInt(5, shelterID);
            preparedStatement.executeUpdate();
        }
    }

    private void insertShelter(Connection connection) throws SQLException {
        String query = "INSERT INTO Shelter (ShelterID, Name, Location, MaxCapacity) VALUES (?, ?, POINT(?, ?), ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, shelterID);
            preparedStatement.setString(2, name);
            preparedStatement.setInt(3, location.getX());
            preparedStatement.setInt(4, location.getY());
            preparedStatement.setInt(5, maxCapacity);
            preparedStatement.executeUpdate();
        }
    }
}
