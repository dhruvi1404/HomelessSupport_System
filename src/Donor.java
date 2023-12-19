import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

public class Donor {
    private int donorID;
    private String name;
    private Point location;
    private String donorType;
    private Set<String> fundingPrograms;

    public Donor(String name, Point location, String donorType, Set<String> fundingPrograms) {
        this.name = name;
        this.location = location;
        this.donorType = donorType;
        this.fundingPrograms = fundingPrograms;
    }

    public int getDonorID() {
        return donorID;
    }

    public void setDonorID(int donorID) {
        this.donorID = donorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    public String getDonorType() {
        return donorType;
    }

    public void setDonorType(String donorType) {
        this.donorType = donorType;
    }

    public Set<String> getFundingPrograms() {
        return fundingPrograms;
    }

    public void setFundingPrograms(Set<String> fundingPrograms) {
        this.fundingPrograms = fundingPrograms;
    }

    public boolean isValid() {
        return name != null && !name.isEmpty() &&
                location != null && Point.isValidPoint(location) &&
                donorType != null && !donorType.isEmpty();
    }

    public boolean saveOrUpdate(Connection connection) {
        if (!isValid() || connection == null) {
            return false;
        }

        try {
            if (donorExistsByName(connection)) {
                updateDonor(connection);
            } else {
                insertDonor(connection);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean donorExistsByName(Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) FROM Donor WHERE Name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, name);
            try (var resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private void updateDonor(Connection connection) throws SQLException {
        String query = "UPDATE Donor SET Location = POINT(?, ?), DonorType = ? WHERE Name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, location.getX());
            preparedStatement.setInt(2, location.getY());
            preparedStatement.setString(3, donorType);
            preparedStatement.setString(4, name);
            preparedStatement.executeUpdate();
        }
    }

    private void insertDonor(Connection connection) throws SQLException {
        String query = "INSERT INTO Donor (Name, Location, DonorType) VALUES (?, POINT(?, ?), ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, location.getX());
            preparedStatement.setInt(3, location.getY());
            preparedStatement.setString(4, donorType);

            preparedStatement.executeUpdate();

            // Retrieve the generated donor ID
            try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    setDonorID(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating donor failed, no ID obtained.");
                }
            }
        }

        // Save funding programs to the database
        String fundingSql = "INSERT INTO FundingProgram (DonorID, ProgramName) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(fundingSql)) {
            for (String program : getFundingPrograms()) {
                preparedStatement.setInt(1, getDonorID());
                preparedStatement.setString(2, program);

                preparedStatement.executeUpdate();
            }
        }
    }
}
