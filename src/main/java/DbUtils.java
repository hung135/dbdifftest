
import java.sql.*;

/**
 * Functions that takes database connections and does something in database
 */
public class DbUtils {

    DbUtils() {
        System.out.println("Constructor called");
    }

    public static String viewValue(Connection con, String command) throws SQLException {
        String value = null;
        Statement stmt = null;

        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(command);

            while (rs.next())
                value = rs.toString();
        }

        catch (SQLException e) {
            e.printStackTrace();
        }

        finally {
            if (stmt != null) {
                stmt.close();
            }
        }

        return value;

    }
}
