import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FundsDisbursement {
    private Connection connection;

    public FundsDisbursement(Connection connection) {
        this.connection = connection;
    }

    public boolean disburseFunds(String shelterReceiving, String date, int funds) {
        try {
            // Validate parameters
            if (shelterReceiving == null || shelterReceiving.isEmpty() || date == null || date.isEmpty() || funds < 0) {
                return false;
            }

            // Check if the shelter exists in the database
            int shelterID = getShelterIDByName(shelterReceiving);

            // Record the funds disbursement in the FundsDisbursement table
            String query = "INSERT INTO FundsDisbursement (ShelterID, Date, Amount) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, shelterID);
                preparedStatement.setString(2, date);
                preparedStatement.setInt(3, funds);
                preparedStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to get ShelterID by name
    private int getShelterIDByName(String shelterName) throws SQLException {
        String query = "SELECT ShelterID FROM Shelter WHERE Name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, shelterName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("ShelterID") : -1;
            }
        }
    }
}
