package main.com.sampleService.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBConnection {
	
	public static Connection getDBConnection() {
		
		Connection conn = null;
		
		try {
			System.out.println("Starting to get the Database connection");
			Class.forName("org.postgresql.Driver");
			
			//conn = DriverManager.getConnection("jdbc:postgresql://10.11.241.0:39809/Z9-RxO000xKfnUuZ", "MVM24Zy8OCeUhwMD", "cuZEsEkmYigC4Yc3");
			
			Map<String, String> mapData = getConnectionUrl();
			
			if (null != mapData) {
				
				String uName = mapData.get("username");
				String pass = mapData.get("passwords");
				String url = mapData.get("url");
				System.out.println("URL is >>> " + url);
				conn = DriverManager.getConnection(url, uName, pass);
				
				System.out.println(" Connection established >>>>>> " + conn);
			} else {
				
				System.out.println(" Credentials not found >>> ");
			}
		}
		catch(Exception e){
			System.out.println("Failed to get the Database connection using postgres Driver ");
			e.printStackTrace();
		}
		
		return conn;
	}
	
	public static Map<String, String> getConnectionUrl() {
		
		String env = System.getenv("VCAP_SERVICES");
		Map<String, String> mapNameCredetials = null;
		if (env != null) {
			mapNameCredetials = new HashMap<String, String>();
			JSONObject obj = new JSONObject(env);
			JSONArray arr = obj.getJSONArray("DistributedServiceBundle");

			String DB_USERNAME = arr.getJSONObject(0).getJSONObject("credentials").getString("username");
			String DB_PASSWORD = arr.getJSONObject(0).getJSONObject("credentials").getString("password");
			String DB_NAME = arr.getJSONObject(0).getJSONObject("credentials").getString("dbname");
			String host = arr.getJSONObject(0).getJSONObject("credentials").getString("hostname");
			int port = arr.getJSONObject(0).getJSONObject("credentials").getInt("port");
			//port = 6432;
			
			String URI = "jdbc:postgresql://"+host+":"+port+"/"+DB_NAME;
			
			mapNameCredetials.put("username", DB_USERNAME);
			mapNameCredetials.put("passwords", DB_PASSWORD);
			mapNameCredetials.put("url", URI);
		}
		return mapNameCredetials;
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
