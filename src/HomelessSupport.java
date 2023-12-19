import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HomelessSupport {
    private Service service;
    private ShelterServiceManager shelterServiceManager;
    private OccupancyManager occupancyManager;
    private Donation donation;
    private FundsDisbursement fundsDisbursement;
    private LocationManager locationManager;
    private Connection connection;

    public HomelessSupport(Connection connection) {
        this.connection = connection;
        this.service = new Service(connection);
        this.shelterServiceManager = new ShelterServiceManager(connection);
        this.occupancyManager = new OccupancyManager(connection);
        this.donation = new Donation(connection);
        this.fundsDisbursement = new FundsDisbursement(connection);
        this.locationManager = new LocationManager(connection);
    }

    public boolean defineService(String serviceName, int inspectionFrequency) {
        return service.defineService(serviceName, inspectionFrequency);
    }

    public boolean defineShelter(int shelterID, String name, Point location, int maxCapacity) {
        Shelter shelter = new Shelter(shelterID, name, location, maxCapacity);
        boolean result = shelter.saveOrUpdate(connection);
        // Save or update location information
        if (result) {
            locationManager.saveOrUpdateLocation(location);
        }
        return result;
    }

    public boolean serviceForShelter(String shelterName, String serviceName) {
        return shelterServiceManager.addServiceToShelter(shelterName, serviceName);
    }

    public boolean declareShelterOccupancy(String name, String date, int occupancy) {
        return occupancyManager.declareShelterOccupancy(name, date, occupancy);
    }

    public boolean addStaff(String name, Set<String> services, boolean volunteer, String manager) {
        Staff staff = new Staff(connection,name, services, volunteer, manager);
        return staff.saveOrUpdate();
    }

    boolean defineDonor(String name, Point centralOffice, String donorType, Set<String> fundingPrograms) {
        Donor donor = new Donor(name, centralOffice, donorType, fundingPrograms);
        boolean result = donor.saveOrUpdate(connection);

        // Save or update location information
        if (result) {
            locationManager.saveOrUpdateLocation(centralOffice);
        }

        return result;
    }
    boolean receiveDonation(String donor, String fundingProgram, String date, int donation) {
        return this.donation.receiveDonation(donor, fundingProgram, date, donation);
    }
    boolean disburseFunds(String shelterReceiving, String date, int funds) {
        return fundsDisbursement.disburseFunds(shelterReceiving, date, funds);
    }
    // Modified shelterAtCapacity method
    public Set<String> shelterAtCapacity(int threshold) {
        Set<String> sheltersAtCapacity = new HashSet<>();

        try {
            // Validate the threshold
            if (threshold < 0 || threshold > 100) {
                System.out.println("Invalid threshold. Threshold must be between 0 and 100.");
                return sheltersAtCapacity;
            }

            // Calculate the effective threshold (as a decimal)
            double effectiveThreshold = threshold / 100.0;

            // Query the shelters with recent occupancy reports
            // Query the shelters with recent occupancy reports
            String query = "SELECT Shelter.Name " +
                    "FROM Shelter " +
                    "LEFT JOIN Occupancy ON Shelter.ShelterID = Occupancy.ShelterID " +
                    "WHERE COALESCE(Occupancy.Date, '1970-01-01') = " +
                    "   (SELECT MAX(COALESCE(Date, '1970-01-01')) FROM Occupancy WHERE ShelterID = Shelter.ShelterID) " +
                    "GROUP BY Shelter.ShelterID " +
                    "HAVING MAX(COALESCE(Occupancy.Occupancy, 0)) >= MAX(Shelter.MaxCapacity) * ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDouble(1, effectiveThreshold);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        sheltersAtCapacity.add(resultSet.getString("Name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sheltersAtCapacity;
    }

    // Modified occupancyVariance method
    Set<String> occupancyVariance(String startDate, String endDate, int threshold) {
        Set<String> result = new HashSet<>();

        try {
            // SQL query to calculate occupancy variance for each shelter
            String query = "SELECT Shelter.Name " +
                    "FROM Shelter " +
                    "LEFT JOIN Occupancy ON Shelter.ShelterID = Occupancy.ShelterID " +
                    "WHERE (Occupancy.Date BETWEEN ? AND ?) OR Occupancy.Date IS NULL " +
                    "GROUP BY Shelter.ShelterID " +
                    "HAVING " +
                    "    MAX(COALESCE(Occupancy.Occupancy, 0)) IS NULL OR " +
                    "    (MAX(COALESCE(Occupancy.Occupancy, 0)) - MIN(COALESCE(Occupancy.Occupancy, 0))) / MAX(Shelter.MaxCapacity) * 100 >= ?";


            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, startDate);
                preparedStatement.setString(2, endDate);
                preparedStatement.setDouble(3, (double) threshold / 100);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(resultSet.getString("Name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
    void donorReport(String startDate, String endDate, PrintWriter outstream) {
        try {
            // SQL query to retrieve donor activity within the specified time range
            String query = "SELECT Donor.Name AS DonorName, " +
                    "       FundingProgram.ProgramName, " +
                    "       SUM(Donation.Amount) AS TotalFunding " +
                    "FROM Donation " +
                    "JOIN Donor ON Donation.DonorID = Donor.DonorID " +
                    "JOIN FundingProgram ON Donation.ProgramID = FundingProgram.ProgramID " +
                    "WHERE Donation.Date BETWEEN ? AND ? " +
                    "GROUP BY Donor.DonorID, FundingProgram.ProgramID " +
                    "ORDER BY Donor.DonorID, FundingProgram.ProgramID";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, startDate);
                preparedStatement.setString(2, endDate);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    String currentDonor = "";
                    Map<String, Integer> programTotalMap = new HashMap<>();

                    while (resultSet.next()) {
                        String donorName = resultSet.getString("DonorName");
                        String programName = resultSet.getString("ProgramName");
                        int totalFunding = resultSet.getInt("TotalFunding");

                        if (!donorName.equals(currentDonor)) {
                            // Start a new reporting block for a new donor
                            if (!currentDonor.isEmpty()) {
                                outstream.println();  // Blank line to separate donors
                            }
                            outstream.println(donorName);
                            currentDonor = donorName;
                            programTotalMap.clear();
                        }

                        // Update total funding for the funding program
                        programTotalMap.put(programName, programTotalMap.getOrDefault(programName, 0) + totalFunding);

                        // Print program details
                        outstream.printf("\t%s\t%d\n", programName, totalFunding);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    Set<String> underfundedShelter(String startDate, String endDate, int distance, int threshold) {
        Set<String> result = new HashSet<>();

        try {
            // SQL query to calculate funding per occupant capacity for each shelter or camp
            String query = "SELECT Shelter.Name, " +
                    "       SUM(FundsDisbursement.Amount) / MAX(Shelter.MaxCapacity) AS FundingPerOccupant " +
                    "FROM FundsDisbursement " +
                    "JOIN Shelter ON FundsDisbursement.ShelterID = Shelter.ShelterID " +
                    "WHERE FundsDisbursement.Date BETWEEN ? AND ? " +
                    "GROUP BY Shelter.ShelterID " +
                    "HAVING FundingPerOccupant < ? " +
                    "ORDER BY FundingPerOccupant";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, startDate);
                preparedStatement.setString(2, endDate);
                preparedStatement.setDouble(3, threshold / 100.0);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        result.add(resultSet.getString("Name"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    Map<String, List<String>> inspectionSchedule(int scheduleDays, int inspectLimit) {
        Map<String, List<String>> schedule = new HashMap<>();

        try {
            // SQL query to get services that need inspection and staff members who can inspect them
            String query = "SELECT ShelterService.ShelterID, Service.ServiceName, Staff.Name " +
                    "FROM ShelterService " +
                    "JOIN Service ON ShelterService.ServiceID = Service.ServiceID " +
                    "JOIN StaffService ON Service.ServiceID = StaffService.ServiceID " +
                    "JOIN Staff ON StaffService.StaffID = Staff.StaffID";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int shelterID = resultSet.getInt("ShelterID");
                        String serviceName = resultSet.getString("ServiceName");
                        String staffName = resultSet.getString("Name");

                        // Create schedule entry if not present
                        schedule.putIfAbsent(staffName, new ArrayList<>());

                        // Add inspection entry for the service and shelter
                        schedule.get(staffName).add(String.format("(%d,%s)", shelterID, serviceName));
                    }
                }
            }

            // Optimize the schedule to meet constraints
            optimizeSchedule(schedule, inspectLimit, scheduleDays);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedule;
    }
    // Helper method to optimize the inspection schedule
    private void optimizeSchedule(Map<String, List<String>> schedule, int inspectLimit, int scheduleDays) {
        for (Map.Entry<String, List<String>> entry : schedule.entrySet()) {
            List<String> inspections = entry.getValue();
            int totalInspections = inspections.size();
            int inspectionsPerDay = Math.min(inspectLimit, totalInspections);
            int daysNeeded = (int) Math.ceil((double) totalInspections / inspectLimit);

            // Initialize the schedule for each day
            List<List<String>> dailySchedule = new ArrayList<>();
            for (int i = 0; i < scheduleDays; i++) {
                dailySchedule.add(new ArrayList<>());
            }

            // Distribute inspections evenly across days
            int inspectionIndex = 0;
            for (int day = 0; day < daysNeeded; day++) {
                for (int i = 0; i < inspectionsPerDay; i++) {
                    if (inspectionIndex < totalInspections) {
                        dailySchedule.get(day % scheduleDays).add(inspections.get(inspectionIndex));
                        inspectionIndex++;
                    }
                }
            }
            // Update the schedule with the optimized inspections
            entry.setValue(new ArrayList<>()); // Clear existing schedule
            for (List<String> daySchedule : dailySchedule) {
                entry.getValue().addAll(daySchedule);
            }
        }
 }
}
