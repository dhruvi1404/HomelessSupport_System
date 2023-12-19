import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Obtain a database connection
            Connection connection = DatabaseConnection.connect();

            // Create an instance of HomelessSupport using the obtained connection
            HomelessSupport homelessSupport = new HomelessSupport(connection);

            // Test Define Shelter
            //testDefineShelter(homelessSupport);

            // Test Define Service
          //testDefineService(homelessSupport);

            // Test Service For Shelter
           // testServiceForShelter(homelessSupport);
            // Test Declare Shelter Occupancy
          //  testDeclareShelterOccupancy(homelessSupport);
            // Test Add Staff
         //testAddStaff(homelessSupport);

            // Test Define Donor
           //testDefineDonor(homelessSupport);

            // Test Receive Donation
           //testReceiveDonation(homelessSupport);

            // Test Disburse Funds
           //testDisburseFunds(homelessSupport);
            // Test Shelter At Capacity
           // testShelterAtCapacity(homelessSupport, 60); // You can set the threshold percentage here
           // Test Occupancy Variance
            //testOccupancyVariance(homelessSupport); // You can set the date range and threshold percentage here

            // Test Create Donors
//            testCreateDonors(homelessSupport);

            // Test Donor Report
          //  testDonorReport(homelessSupport);
            // Test Underfunded Shelters
            //testUnderfundedShelters(homelessSupport);
            // Test Inspection Schedule
            testInspectionSchedule(homelessSupport, 7, 2); // You can adjust scheduleDays and inspectLimit as needed

            // Close the connection
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void testDefineShelter(HomelessSupport homelessSupport) {
        System.out.println("Testing Define Shelter...");

        // Define Shelter A
        boolean test1Result = homelessSupport.defineShelter(1, "Shelter A", new Point(1, 2), 50);
        System.out.println("Test 1 Result: " + test1Result);

        // Define Shelter B
        boolean test2Result = homelessSupport.defineShelter(2, "Shelter B", new Point(3, 4), 75);
        System.out.println("Test 2 Result: " + test2Result);

        // Define Shelter C
        boolean test3Result = homelessSupport.defineShelter(3, "Shelter C", new Point(5, 6), 100);
        System.out.println("Test 3 Result: " + test3Result);

        // Define Shelter D
        boolean test4Result = homelessSupport.defineShelter(4, "Shelter D", new Point(7, 8), 120);
        System.out.println("Test 4 Result: " + test4Result);

        System.out.println();
    }
    private static void testDefineService(HomelessSupport homelessSupport) {
        System.out.println("Testing Define Service...");

        // Define Shelter Cleaning service with inspection frequency 14
        boolean test1Result = homelessSupport.defineService("Shelter Cleaning", 14);
        System.out.println("Test 1 Result: " + test1Result);

        // Define Medical Assistance service with inspection frequency 30
        boolean test2Result = homelessSupport.defineService("Medical Assistance", 30);
        System.out.println("Test 2 Result: " + test2Result);

        // Define Counseling service with inspection frequency 21
        boolean test3Result = homelessSupport.defineService("Counseling", 21);
        System.out.println("Test 3 Result: " + test3Result);

        // Define Food Distribution service with inspection frequency 7
        boolean test4Result = homelessSupport.defineService("Food Distribution", 7);
        System.out.println("Test 4 Result: " + test4Result);

        System.out.println();
    }
    private static void testServiceForShelter(HomelessSupport homelessSupport) {
        System.out.println("Testing Service For Shelter...");

        // Connect Shelter A with Shelter Cleaning service
        boolean test1Result = homelessSupport.serviceForShelter("Shelter A", "Shelter Cleaning");
        System.out.println("Test 1 Result: " + test1Result);

        // Connect Shelter A with Food Distribution service
        boolean test2Result = homelessSupport.serviceForShelter("Shelter A", "Food Distribution");
        System.out.println("Test 2 Result: " + test2Result);

        // Connect Shelter B with Medical Assistance service
        boolean test3Result = homelessSupport.serviceForShelter("Shelter B", "Medical Assistance");
        System.out.println("Test 3 Result: " + test3Result);

        // Connect Shelter C with Counseling service
        boolean test4Result = homelessSupport.serviceForShelter("Shelter C", "Counseling");
        System.out.println("Test 4 Result: " + test4Result);

        System.out.println();
    }
    private static void testDeclareShelterOccupancy(HomelessSupport homelessSupport) {
        System.out.println("Testing Declare Shelter Occupancy...");

        // Assume today's date for testing (replace with the actual date)
        Date currentDate = new Date(System.currentTimeMillis());

        // Format the date as a string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(currentDate);

        // Declare Shelter A occupancy for today
        boolean test1Result = homelessSupport.declareShelterOccupancy("Shelter A", dateString, 30);
        System.out.println("Test 1 Result: " + test1Result);

        // Declare Shelter B occupancy for today
        boolean test2Result = homelessSupport.declareShelterOccupancy("Shelter B", dateString, 40);
        System.out.println("Test 2 Result: " + test2Result);

        System.out.println();
    }
    private static void testAddStaff(HomelessSupport homelessSupport) {
        System.out.println("Testing Add Staff...");

        // Add staff member John Doe with role "Counselor" and other parameters
        Set<String> johnDoeServices = new HashSet<>();
        johnDoeServices.add("Counseling");
        boolean test1Result = homelessSupport.addStaff("John Doe", johnDoeServices, false, "ManagerA");
        System.out.println("Test 1 Result: " + test1Result);

        // Add staff member Jane Smith with role "Medical Assistant" and other parameters
        Set<String> janeSmithServices = new HashSet<>();
        janeSmithServices.add("Medical Assistance");
        boolean test2Result = homelessSupport.addStaff("Jane Smith", janeSmithServices, true, "ManagerB");
        System.out.println("Test 2 Result: " + test2Result);

        System.out.println();
    }
    private static void testDefineDonor(HomelessSupport homelessSupport) throws SQLException {
        System.out.println("Testing Define Donor...");

        // Define Donor X
        boolean test1Result = homelessSupport.defineDonor("Donor X", new Point(10, 20), "Individual", Set.of("Cash Donation"));
        System.out.println("Test 1 Result: " + test1Result);

        // Define Donor Y
        boolean test2Result = homelessSupport.defineDonor("Donor Y", new Point(30, 40), "Organization", Set.of("Project Funding", "Emergency Relief"));
        System.out.println("Test 2 Result: " + test2Result);

        // Define Donor Z
        boolean test3Result = homelessSupport.defineDonor("Donor Z", new Point(50, 60), "Corporate", Set.of("Corporate Sponsorship", "Employee Giving"));
        System.out.println("Test 3 Result: " + test3Result);

        System.out.println();
    }
    private static void testReceiveDonation(HomelessSupport homelessSupport) {
        System.out.println("Testing Receive Donation...");

        // Assume today's date for testing (replace with the actual date)
        Date currentDate = new Date(System.currentTimeMillis());

        // Format the date as a string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(currentDate);

        // Receive donation from Donor X to funding program "Cash Donation" for Shelter A
        boolean test1Result = homelessSupport.receiveDonation("Donor X", "Cash Donation", dateString, 500);
        System.out.println("Test 1 Result: " + test1Result);

        // Receive donation from Donor Y to funding program "Project Funding" for Shelter B
        boolean test2Result = homelessSupport.receiveDonation("Donor Y", "Project Funding", dateString, 1000);
        System.out.println("Test 2 Result: " + test2Result);

        // Receive donation from Donor Z to funding program "Corporate Sponsorship" for Shelter C
        boolean test3Result = homelessSupport.receiveDonation("Donor Z", "Corporate Sponsorship", dateString, 1500);
        System.out.println("Test 3 Result: " + test3Result);

        System.out.println();
    }
    private static void testDisburseFunds(HomelessSupport homelessSupport) {
        System.out.println("Testing Disburse Funds...");

        // Assume today's date for testing (replace with the actual date)
        Date currentDate = new Date(System.currentTimeMillis());

        // Format the date as a string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(currentDate);

        // Disburse funds to Shelter A
        boolean testResult = homelessSupport.disburseFunds("Shelter A", dateString, 2000);
        System.out.println("Test Result: " + testResult);

        System.out.println();
    }
    private static void testShelterAtCapacity(HomelessSupport homelessSupport, int threshold) {
        System.out.println("Testing Shelter At Capacity...");

        // Call the shelterAtCapacity method and get the result
        Set<String> sheltersAtCapacity = homelessSupport.shelterAtCapacity(threshold);

        // Print the result
        System.out.println("Shelters at or above " + threshold + "% capacity:");
        for (String shelter : sheltersAtCapacity) {
            System.out.println(shelter);
        }

        System.out.println();
    }
    private static void testOccupancyVariance(HomelessSupport homelessSupport) {
        System.out.println("Testing Occupancy Variance...");

        // Assume today's date for testing (replace with the actual date)
        Date currentDate = new Date(System.currentTimeMillis());

        // Format the date as a string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(currentDate);

        // Add occupancy data for today
        boolean test1Result = homelessSupport.declareShelterOccupancy("Shelter A", startDate, 30);
        boolean test2Result = homelessSupport.declareShelterOccupancy("Shelter B", startDate, 40);
        boolean test3Result = homelessSupport.declareShelterOccupancy("Shelter C", startDate, 80);
        boolean test4Result = homelessSupport.declareShelterOccupancy("Shelter D", startDate, 110);

        // Add occupancy data for tomorrow
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String endDate = sdf.format(calendar.getTime());

        boolean test5Result = homelessSupport.declareShelterOccupancy("Shelter A", endDate, 35);
        boolean test6Result = homelessSupport.declareShelterOccupancy("Shelter B", endDate, 45);
        boolean test7Result = homelessSupport.declareShelterOccupancy("Shelter C", endDate, 90);
        boolean test8Result = homelessSupport.declareShelterOccupancy("Shelter D", endDate, 120);

        // Call the occupancyVariance method and get the result
        Set<String> highVarianceShelters = homelessSupport.occupancyVariance(startDate, endDate, 10);

        // Print the result
        System.out.println("Shelters with high occupancy variance:");
        for (String shelter : highVarianceShelters) {
            System.out.println(shelter);
        }

        System.out.println();
    }
    private static void testCreateDonors(HomelessSupport homelessSupport) {
        System.out.println("Testing Create Donors...");

        // Define Donor X
        boolean test1Result = homelessSupport.defineDonor("Donor X", new Point(10, 20), "Individual", Set.of("Cash Donation"));
        System.out.println("Test 1 Result: " + test1Result);

        // Define Donor Y
        boolean test2Result = homelessSupport.defineDonor("Donor Y", new Point(30, 40), "Organization", Set.of("Project Funding", "Emergency Relief"));
        System.out.println("Test 2 Result: " + test2Result);

        // Define Donor Z
        boolean test3Result = homelessSupport.defineDonor("Donor Z", new Point(50, 60), "Corporate", Set.of("Corporate Sponsorship", "Employee Giving"));
        System.out.println("Test 3 Result: " + test3Result);

        System.out.println();
    }
    private static void testDonorReport(HomelessSupport homelessSupport) {
        System.out.println("Testing Donor Report...");

        // Assume today's date for testing (replace with the actual date)
        Date currentDate = new Date(System.currentTimeMillis());

        // Format the date as a string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(currentDate);

        // Add donation data for today
        boolean test1Result = homelessSupport.receiveDonation("Donor X", "Cash Donation", startDate, 500);
        boolean test2Result = homelessSupport.receiveDonation("Donor Y", "Project Funding", startDate, 1000);
        boolean test3Result = homelessSupport.receiveDonation("Donor Z", "Corporate Sponsorship", startDate, 1500);

        // Add donation data for tomorrow
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        String endDate = sdf.format(calendar.getTime());

        boolean test4Result = homelessSupport.receiveDonation("Donor X", "Cash Donation", endDate, 700);
        boolean test5Result = homelessSupport.receiveDonation("Donor Y", "Project Funding", endDate, 1200);
        boolean test6Result = homelessSupport.receiveDonation("Donor Z", "Corporate Sponsorship", endDate, 1800);

        // Create a PrintWriter to capture the output
        try (PrintWriter outstream = new PrintWriter(new FileWriter("donor_report.txt"))) {
            // Call the donorReport method
            homelessSupport.donorReport(startDate, endDate, outstream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Donor report generated in 'donor_report.txt'");
        System.out.println();
    }
    private static void testUnderfundedShelters(HomelessSupport homelessSupport) {
        System.out.println("Testing Underfunded Shelters...");

        // Assume today's date for testing (replace with the actual date)
        Date currentDate = new Date(System.currentTimeMillis());

        // Format the date as a string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(currentDate);

        // Add occupancy data for today
        boolean test1Result = homelessSupport.declareShelterOccupancy("Shelter A", startDate, 30);
        boolean test2Result = homelessSupport.declareShelterOccupancy("Shelter B", startDate, 40);
        boolean test3Result = homelessSupport.declareShelterOccupancy("Shelter C", startDate, 80);
        boolean test4Result = homelessSupport.declareShelterOccupancy("Shelter D", startDate, 110);

        // Add donation data for today
        boolean test5Result = homelessSupport.receiveDonation("Donor X", "Cash Donation", startDate, 500);
        boolean test6Result = homelessSupport.receiveDonation("Donor Y", "Project Funding", startDate, 1000);
        boolean test7Result = homelessSupport.receiveDonation("Donor Z", "Corporate Sponsorship", startDate, 1500);

        // Call the underfundedShelters method and get the result
        Set<String> underfundedShelters = homelessSupport.underfundedShelter(startDate, startDate, 10, 60);


        System.out.println("Underfunded Shelters:");
        for (String shelter : underfundedShelters) {
            System.out.println(shelter);
        }

        System.out.println();
    }
    private static void testInspectionSchedule(HomelessSupport homelessSupport, int scheduleDays, int inspectLimit) {
        System.out.println("Testing Inspection Schedule...");

        // Add some staff members
        Set<String> johnDoeServices = new HashSet<>();
        johnDoeServices.add("Counseling");
        homelessSupport.addStaff("John Doe", johnDoeServices, false, "ManagerA");

        Set<String> janeSmithServices = new HashSet<>();
        janeSmithServices.add("Medical Assistance");
        homelessSupport.addStaff("Jane Smith", janeSmithServices, true, "ManagerB");

        // Connect shelters with services
        homelessSupport.serviceForShelter("Shelter A", "Counseling");
        homelessSupport.serviceForShelter("Shelter A", "Food Distribution");
        homelessSupport.serviceForShelter("Shelter B", "Medical Assistance");

        // Assume today's date for testing
        Date currentDate = new Date(System.currentTimeMillis());

        // Format the date as a string
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(currentDate);

        // Add some occupancy data for today
        homelessSupport.declareShelterOccupancy("Shelter A", startDate, 30);
        homelessSupport.declareShelterOccupancy("Shelter B", startDate, 40);

        // Receive donation from Donor X to funding program "Cash Donation" for Shelter A
        homelessSupport.receiveDonation("Donor X", "Cash Donation", startDate, 500);
        homelessSupport.receiveDonation("Donor Y", "Project Funding", startDate, 1000);

        // Call the inspectionSchedule method and get the result
        Map<String, List<String>> inspectionSchedule = homelessSupport.inspectionSchedule(scheduleDays, inspectLimit);

        // Log intermediate results
        System.out.println("Intermediate Results:");
        System.out.println("inspectionSchedule: " + inspectionSchedule);

        // Print the result
        System.out.println("Inspection Schedule:");
        for (Map.Entry<String, List<String>> entry : inspectionSchedule.entrySet()) {
            System.out.println("Staff Member: " + entry.getKey());
            List<String> dailyInspections = entry.getValue();
            for (int day = 1; day <= dailyInspections.size(); day++) {
                System.out.println("Day " + day + ": " + dailyInspections.get(day - 1));
            }
            System.out.println();
        }

        System.out.println();
    }
}
