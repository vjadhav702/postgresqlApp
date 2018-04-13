package main.com.twitterStream;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import main.com.sampleService.model.DBConnection;
import twitter4j.TwitterException;

public class StreamTwitterData {
	
	public static final String TABLE_NAME = "test";
	public static final String INSERT_TABLE_QUERY = "Insert into " + TABLE_NAME
			+ " (name) values "
			+ " (?)";
	public static final String CREATE_TABLE_QUERY = "create table " + TABLE_NAME 
			+ " (name varchar(10)) ";
	
	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static SecureRandom rnd = new SecureRandom();
	
	static Connection conn = null;
	static int count = 0;
	static boolean status = true;
	static Timestamp prevT = null;
	static Timestamp finalT = null;
	//static Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
	
	public static void main(String[] args) {
		
		System.out.println("Inside main >>>>>>>>>>>>>>>>>>>> ");
		
		conn = DBConnection.getDBConnection();
		
		while(true) {
			
			if (conn != null) {
				checkState();
			} else {
				System.out.println("Creating connection again ......");
				status = false;
				conn = DBConnection.getDBConnection();
			}
			//conn = DBConnection.getDBConnection();
			
			/*if (null != conn)
				try {
					//pushDataToPostgres();
					checkState();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Error >>>>>>>>>>  : " + e);
				} finally {
					if (null != conn)
						DBConnection.closeDBConnection(conn);
				}
			conn = null;*/
		}
   }
	
	public static boolean tableExist(String tableName) throws SQLException {
	    boolean tExists = false;
	    ResultSet rs = conn.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"});
        while (rs.next()) { 
            String tName = rs.getString("TABLE_NAME");
            if (tName != null && tName.equals(tableName.toLowerCase())) {
                tExists = true;
                break;
            }
        }
	    return tExists;
	}
	
	public static void createTable() {
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(CREATE_TABLE_QUERY);
			pstmt.executeUpdate();
			System.out.println("Created data table..");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Table already exists");
			//e.printStackTrace();
		}
	}
	
	public static String randomString( int len ){
		   StringBuilder sb = new StringBuilder( len );
		   for( int i = 0; i < len; i++ ) 
		      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		   return sb.toString();
		}
	
	public static void checkState() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select pg_is_in_recovery()");
			
			while(rs.next()){
				boolean stat = rs.getBoolean("pg_is_in_recovery");
				if (count >= 10 && !status) {
					long diffTime = (finalT.getTime() - prevT.getTime())/1000;
					System.out.println("Failover time =========> " + diffTime + " sec");
					System.exit(0);
				}
				if (!status) {
					count++;
					if (count == 1) {
						finalT = new Timestamp(System.currentTimeMillis());
					}
				} else {
					prevT = new Timestamp(System.currentTimeMillis());
				}
				if (!stat) {
					if (status) {
						System.out.println("Old Master Node" + " >> " + timestamp);
					} else {
						System.out.println("New Master Node" + " >> " + timestamp);
					}
				} else {
					System.out.println("Status is : " + status+" >> Slave Node" + " >> " + timestamp);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in getting the data from table >>>>>>> " + timestamp);
			conn = null;
		}
	}
	
	public static void pushDataToPostgres() throws SQLException {
		
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		if (null != conn) {
			
			boolean flag = tableExist(TABLE_NAME);
			
			if (!flag) {
				//Table not present create table
				createTable();
			}
			
			try {
				PreparedStatement pstmt = conn.prepareStatement(INSERT_TABLE_QUERY);
				
				String data = randomString(9);
				
				pstmt.setString(1, data);
				
				pstmt.executeUpdate();
				
				System.out.println(" inserted data >>>>>>> " + data);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error in inserting data...." + " >>> " + timestamp );
				//e.printStackTrace();
			}
			
		} else {
			System.out.println(" Could not connect !! >>>> " + " >>> " + timestamp );
		}
	}
}
