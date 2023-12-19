import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Donation {
    private Connection connection;

    public Donation(Connection connection) {
        this.connection = connection;
    }

    public boolean receiveDonation(String donor, String fundingProgram, String date, int donation) {
        try {
            // Validate parameters
            if (donor == null || donor.isEmpty() || fundingProgram == null || fundingProgram.isEmpty() ||
                    date == null || date.isEmpty() || donation < 0) {
                return false;
            }

            // Check if the donor exists in the database or insert if not
            int donorID = getOrCreateDonorIDByName(donor);

            // Check if the funding program exists in the database or insert if not
            int programID = getOrCreateProgramIDByName(fundingProgram);

            // Insert new donation
            String insertQuery = "INSERT INTO Donation (DonorID, ProgramID, Date, Amount) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setInt(1, donorID);
                insertStatement.setInt(2, programID);
                insertStatement.setString(3, date);
                insertStatement.setInt(4, donation);
                insertStatement.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Helper method to get DonorID by name or insert if not exists
    private int getOrCreateDonorIDByName(String donorName) throws SQLException {
        int donorID = getDonorIDByName(donorName);

        if (donorID == -1) {
            // Donor does not exist, insert new donor
            donorID = insertDonorAndGetID(donorName);
        }

        return donorID;
    }

    // Helper method to get ProgramID by name or insert if not exists
    private int getOrCreateProgramIDByName(String programName) throws SQLException {
        int programID = getProgramIDByName(programName);

        if (programID == -1) {
            // Program does not exist, insert new program
            programID = insertProgramAndGetID(programName);
        }

        return programID;
    }

    // Helper method to get DonorID by name
    private int getDonorIDByName(String donorName) throws SQLException {
        String query = "SELECT DonorID FROM Donor WHERE Name = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, donorName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("DonorID") : -1;
            }
        }
    }

    // Helper method to get ProgramID by name
    private int getProgramIDByName(String programName) throws SQLException {
        String query = "SELECT ProgramID FROM FundingProgram WHERE ProgramName = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, programName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt("ProgramID") : -1;
            }
        }
    }

    // Helper method to insert a new donor and get the generated ID
    private int insertDonorAndGetID(String donorName) throws SQLException {
        String query = "INSERT INTO Donor (Name) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, donorName);
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                return generatedKeys.next() ? generatedKeys.getInt(1) : -1;
            }
        }
    }

    // Helper method to insert a new program and get the generated ID
    private int insertProgramAndGetID(String programName) throws SQLException {
        String query = "INSERT INTO FundingProgram (ProgramName) VALUES (?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, programName);
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                return generatedKeys.next() ? generatedKeys.getInt(1) : -1;
            }
        }
    }
}
