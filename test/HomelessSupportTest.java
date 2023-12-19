import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HomelessSupportTest {
    private static Connection sharedConnection;  // Shared connection for all tests
    private Service service;
    private ShelterServiceManager shelterServiceManager;
    private HomelessSupport homelessSupport;

    @BeforeAll
    static void setUpBeforeAll() {
        // Set up the shared database connection before all tests
        sharedConnection = DatabaseConnection.connect();
    }

    @BeforeEach
    void setUp() {
        // Initialize the fields before each test method
        service = new Service(sharedConnection);
        shelterServiceManager = new ShelterServiceManager(sharedConnection);
        homelessSupport = new HomelessSupport(sharedConnection);
    }

    @Test
    void defineServiceTest() {
        // Mocking a database connection for testing
        try (Connection connection = DatabaseConnection.connect()) {
            Service service = new Service(connection);
            HomelessSupport homelessSupport = new HomelessSupport(connection);

            // Test regular case
            assertTrue(homelessSupport.defineService("Shelter Service", 2), "defineService test for regular case");

            // Test updating inspection frequency
            assertTrue(homelessSupport.defineService("Shelter Service", 5), "defineService test for updating inspection frequency");

            // Test edge case: minimum inspection frequency
            assertTrue(homelessSupport.defineService("Service A", 1), "defineService test for minimum inspection frequency");

            // Test edge case: maximum inspection frequency
            assertTrue(homelessSupport.defineService("Service B", Integer.MAX_VALUE), "defineService test for maximum inspection frequency");

            // Test edge case: zero inspection frequency (service does not need inspection)
            assertTrue(homelessSupport.defineService("Service C", 0), "defineService test for zero inspection frequency");

            // Test edge case: empty service name
            assertFalse(homelessSupport.defineService("", 3), "defineService test for empty service name");

            // Test edge case: null service name
            assertFalse(homelessSupport.defineService(null, 3), "defineService test for null service name");

            // Test edge case: negative inspection frequency
            assertFalse(homelessSupport.defineService("Service D", -2), "defineService test for negative inspection frequency");

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error setting up a test database connection.");
        }
    }

    @Test
    void testDefineShelter() {
        // Test Case 1: Valid shelter creation
        assertTrue(homelessSupport.defineShelter(1, "Shelter A", new Point(100, 200), 50));

        // Test Case 2: Invalid shelter (negative capacity)
        assertFalse(homelessSupport.defineShelter(2, "Shelter B", new Point(150, 250), -10));

        // Test Case 3: Invalid shelter (null name)
        assertFalse(homelessSupport.defineShelter(3, null, new Point(200, 300), 30));

        // Test Case 4: Valid shelter update (assuming shelter with ID 1 exists)
        assertTrue(homelessSupport.defineShelter(1, "Updated Shelter A", new Point(120, 220), 60));
        // Test Case 5: Valid shelter creation
        assertTrue(homelessSupport.defineShelter(3, "Shelter B", new Point(160, 280), 150));
    }
    @Test
    void testServiceForShelter() {
        // Assuming "Service A" and "Updated Shelter A" exist in the database
        assertTrue(homelessSupport.serviceForShelter("Updated Shelter A", "Service A"),
                "Valid serviceForShelter test");

        // Test Case: Invalid shelter name
        assertFalse(homelessSupport.serviceForShelter("Nonexistent Shelter", "Service A"),
                "Invalid shelter name");

        // Test Case: Invalid service name
        assertFalse(homelessSupport.serviceForShelter("Updated Shelter A", "Nonexistent Service"),
                "Invalid service name");
    }
    @Test
    void testDeclareShelterOccupancy() {
        try (Connection connection = DatabaseConnection.connect()) {
            HomelessSupport homelessSupport = new HomelessSupport(connection);

            // Test Case 1: Valid occupancy declaration
            assertTrue(homelessSupport.declareShelterOccupancy("Updated Shelter A", "2023-12-01", 50),
                    "Valid declareShelterOccupancy test");

            // Test Case 2: Invalid shelter name
            assertFalse(homelessSupport.declareShelterOccupancy("Nonexistent Shelter", "2023-12-01", 30),
                    "Invalid shelter name");

            // Test Case 3: Invalid date format
            assertFalse(homelessSupport.declareShelterOccupancy("Updated Shelter A", "2023/12/01", 40),
                    "Invalid date format");
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Error setting up a test database connection.");
        }
    }
    @Test
    void testAddStaffValidInput() {
        assertTrue(homelessSupport.addStaff("John Doe", Set.of("Service1", "Service2"), false, "Manager1"));
    }

    @Test
    void testAddStaffVolunteerWithoutManager() {
        // Test Case: Volunteer staff without manager
        assertTrue(homelessSupport.addStaff("Volunteer", Set.of("Service1", "Service2"), true, null));
    }

    @Test
    void testAddStaffEmptyName() {
        // Test Case: Empty staff name
        assertFalse(homelessSupport.addStaff("", Set.of("Service1", "Service2"), false, "Manager1"));
    }

    @Test
    void testAddStaffNullServices() {
        // Test Case: Null services
        assertFalse(homelessSupport.addStaff("Jane Smith", null, false, "Manager1"));
    }

    @Test
    void testAddStaffEmptyServices() {
        // Test Case: Empty services
        assertFalse(homelessSupport.addStaff("Alice Johnson", Set.of(), false, "Manager1"));
    }

    @Test
    void testAddStaffNullManager() {
        // Test Case: Null manager (volunteer staff)
        assertTrue(homelessSupport.addStaff("Bob Miller", Set.of("Service1", "Service2"), true, null));
    }

    @Test
    void testAddStaffEmptyManager() {
        // Test Case: Empty manager (non-volunteer staff)
        assertFalse(homelessSupport.addStaff("Charlie Brown", Set.of("Service1", "Service2"), false, ""));
    }



}
