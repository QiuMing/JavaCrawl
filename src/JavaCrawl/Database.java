
package JavaCrawl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	public static String driver = "com.mysql.jdbc.Driver";

	public static String url = "jdbc:mysql://127.0.0.1:3306/tele_dat?autoReconnect=true&failOverReadOnly=false&maxReconnects=40&characterEncoding=UTF-8";
	public static String user = "root";
	public static String password = "";
	public static Statement statement = null;
	public static java.sql.Connection conn = null;

	public static Connection connectToDataBase() {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return (Connection) conn;
	}

	public static void datatoMySql(String sql) throws SQLException {
		conn = connectToDataBase();
		statement = conn.createStatement();
		statement.executeUpdate(sql);
	}

	public static void close() throws SQLException {
		statement.close(); 
		conn.close(); 
	}

	public static void main(String args[]) {
		Database.connectToDataBase();
	}
}
