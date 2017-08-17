package main.com.sampleService.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	public static Connection getDBConnection() {
		
		Connection conn = null;
		
		try {
			System.out.println("Starting to get the Database connection");
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection("jdbc:postgresql://10.11.241.0:55561/QhXXUlFw7O5dpn1s", "Afha-dJfrd1N2k4y", "svYO6WPRVluKq_oO");
			System.out.println(" Connection established >>>>>> " + conn);	
		}
		catch(Exception e){
			System.out.println("Failed to get the Database connection using postgres Driver ");
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public static void closeDBConnection(Connection conn) {
		
		try {
			conn.close();
			System.out.println("Database connection closed >>>> ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("problems closing connection");
			e.printStackTrace();
		}
		
	}
}
