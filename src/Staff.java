import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class Staff {
    private Connection connection;
    private int staffID;  // Assuming there is a StaffID field in the database
    private String name;
    private Set<String> services;
    private boolean volunteer;
    private String manager;

    public Staff(Connection connection, String name, Set<String> services, boolean volunteer, String manager) {
        this.connection = connection;
        this.name = name;
        this.services = services;
        this.volunteer = volunteer;
        this.manager = manager;
    }

    public boolean saveOrUpdate() {
        try {
            // Validate parameters
            if (name == null || name.isEmpty() || manager == null || manager.isEmpty()) {
                System.out.println("Invalid input. Name and Manager are required.");
                return false;
            }

            // Check if the staff with the same name already exists
            int existingStaffID = getStaffIDByName(this.name);

            if (existingStaffID != -1) {
                // Staff with the same name exists, update the record
                String updateQuery = "UPDATE Staff SET Volunteer = ?, Manager = ? WHERE StaffID = ?";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
                    updateStatement.setBoolean(1, this.volunteer);
                    updateStatement.setString(2, this.manager);
                    updateStatement.setInt(3, existingStaffID);
                    updateStatement.executeUpdate();
                    return true;
                }
            } else {
                // Staff does not exist, insert new staff
                String insertQuery = "INSERT INTO Staff (Name, Volunteer, Manager) VALUES (?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    insertStatement.setString(1, this.name);
                    insertStatement.setBoolean(2, this.volunteer);
                    insertStatement.setString(3, this.manager);
                    insertStatement.executeUpdate();
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to get StaffID by name
    private int getStaffIDByName(String staffName) throws SQLException {
        String query = "SELECT StaffID FROM Staff WHERE Name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, staffName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("StaffID") : -1;
            }
        }
    }
}
