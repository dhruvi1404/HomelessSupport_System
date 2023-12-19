import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection {

    public static Connection connect() {

        // Get my identity information

        Properties identity = new Properties();
        String username = "";
        String password = "";
        String propertyFilename = "C:\\Users\\sdhru\\OneDrive\\Documents\\SDC Project\\shah14\\src\\Property.prop";

        try {
            InputStream stream = new FileInputStream( propertyFilename );

            identity.load(stream);

            username = identity.getProperty("username");
            password = identity.getProperty("password");
        } catch (Exception e) {
            throw new RuntimeException("Database connection failed");
        }

        // Do the actual database work now

        Connection connect = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connect = DriverManager.getConnection("jdbc:mysql://db.cs.dal.ca:3306?serverTimezone=UTC&useSSL=false", username, password );
            statement = connect.createStatement();
            statement.execute("use shah14;");
            System.out.println("Connection Esatblished");

            return connect;

        } catch (Exception e) {
            // Should do better than catching generic "Exception" and of
            // dealing with the situation by printing to the screen.  As the
            // point here is to test the database connection, I'll take the
            // compromise.

            System.out.println("Connection failed");
            throw new RuntimeException("Connection failed");
        }
    }
}
