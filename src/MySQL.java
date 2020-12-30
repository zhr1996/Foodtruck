import java.sql.*;

public class MySQL {
    public static Connection conn;

    public static void connectToMySQL() throws SQLException {
        String dbURL = "jdbc:mysql://localhost:3306/cs4400spring2020";
        String user = "root";
        String password = "12345678"; //fix here
        conn = DriverManager.getConnection(dbURL, user, password);

    }


    // INPUT: String query = "{? = CALL procedureName(?, ?)}";
    // OUTPUT: String query = "{? = CALL procedureName(?, ?)}";
    public static ResultSet table(String query) {
        ResultSet result = null;
        try {
            Statement statement = conn.createStatement();
            result = statement.executeQuery(query);
        } catch (SQLException ex) {
            System.out.println("An error occurred while calling query");
        }
        return result;
    }

    // String query = "{call deptSelect(?,?,?)}
    // CallableStatement statement = conn.prepareCall(query);
    // statement.registerOutParameter(1, Types.VARCHAR);
    // statement.setString(2, office);
    // statement.getString(1);
    public static void procedure(CallableStatement statement) {
        try {
            statement.execute();
        } catch (SQLException ex) {
            System.out.println("An error occurred while calling procedure");
        }
    }
}
