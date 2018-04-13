package main.com.sampleService.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class DBConnection {
	
	public static Connection getDBConnection() {
		
		Connection connt = null;
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		try {
			Class.forName("org.postgresql.Driver");
			
			//conn = DriverManager.getConnection("jdbc:postgresql://10.11.241.0:39809/Z9-RxO000xKfnUuZ", "MVM24Zy8OCeUhwMD", "cuZEsEkmYigC4Yc3");
			
			Map<String, String> mapData = getConnectionUrl();
			
			if (null != mapData) {
				
				String uName = mapData.get("username");
				String pass = mapData.get("passwords");
				String url = mapData.get("url");
				connt = DriverManager.getConnection(url, uName, pass);
				
			} else {
				
				System.out.println(" Credentials not found >>> ");
			}
		}
		catch(Exception e){
			System.out.println("Failed to get the Database connection using postgres Driver >>> " + timestamp);
			connt = null;
			//e.printStackTrace();
			//Revoker r = new RevokerImpl();
			//boolean st = r.revokeDNSCacheEntry("postgresNLB1-fdb98b532cb587b1.elb.eu-west-1.amazonaws.com");
			//System.out.println("status is : " + st);
		}
		
		return connt;
	}
	
	public static Map<String, String> getConnectionUrl() {
		
		Map<String, String> mapNameCredetials = null;
		mapNameCredetials = new HashMap<String, String>();

		String DB_USERNAME = "vcap";//arr.getJSONObject(0).getJSONObject("credentials").getString("username");
		String DB_PASSWORD = "vcap";//arr.getJSONObject(0).getJSONObject("credentials").getString("password");
		String DB_NAME = "postgres";//arr.getJSONObject(0).getJSONObject("credentials").getString("dbname");
		String host = "10.11.2.133";//arr.getJSONObject(0).getJSONObject("credentials").getString("hostname");
		int port = 5432;//arr.getJSONObject(0).getJSONObject("credentials").getInt("port");
		String URI = "jdbc:postgresql://"+host+":"+port+"/"+DB_NAME;
		
		mapNameCredetials.put("username", DB_USERNAME);
		mapNameCredetials.put("passwords", DB_PASSWORD);
		mapNameCredetials.put("url", URI);
		
		return mapNameCredetials;
	}
	
	public static void closeDBConnection(Connection conn) {
		
		if (null != conn)
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("problems closing connection");
				e.printStackTrace();
			}
		else {
			System.out.println(" No connection wait ......");
		}
		
	}
}
