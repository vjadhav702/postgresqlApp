package main.com.urlencoder.servletController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import main.com.sampleService.model.DBConnection;

/**
 * Servlet implementation class URLConverter
 */
@WebServlet("/URLConverter")
public class URLConverter extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
	public static Connection conn = null;
	
	public static final String CREATE_TABLE_QUERY = "create table hashData "
			+ " ( "
			+ " h_value varchar(64) NOT NULL,"
			+ " h_url varchar(64),"
			+ " PRIMARY KEY (h_value)"
			+ " )";
	
	public static final String DELETE_TABLE_QUERY = "drop table hashData";
	
	public static final String INSERT_TABLE_QUERY = "Insert into hashData "
			+ " (h_value, h_url) values "
			+ " (?, ?)";
	
	public static final String DELETE_FROM_TABLE_QUERY = "Delete from hashData "
			+ "where h_value = ?";
	
	public static final String SHOW_DATA_FROM_TABLE_QUERY = "select * from hashData";
	
	public static final String GET_URL_QUERY = "select h_url from hashData where h_value = ?";
	
	/**
     * @see HttpServlet#HttpServlet()
     */
    public URLConverter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String data = request.getParameter("userName").trim();
		
		String tData[] = data.split("####");
		
		if (tData[0].equalsIgnoreCase("create")) {
			
			String res = createTable();
			response.setContentType("text/plain");
			response.getWriter().write(res);
		} else if (tData[0].equalsIgnoreCase("insert")) {
			
			String h_val = "";
			String h_url = tData[1];
			
			//generateHash....
			h_val = generateHash(h_url);
			String res = insertIntoTable(h_val, h_url);
			response.setContentType("text/plain");
			response.getWriter().write(res);
		} else if (tData[0].equalsIgnoreCase("delete")) {
			
			String h_val = tData[1];
			String res = deleteEntryFromTable(h_val);
			response.setContentType("text/plain");
			response.getWriter().write(res);
		} else if (tData[0].equalsIgnoreCase("drop")) {
			
			String res = deleteTable();
			response.setContentType("text/plain");
			response.getWriter().write(res);
		} else if (tData[0].equalsIgnoreCase("getUrl")){
			
			String h_value = tData[1];
			String res = getUrl(h_value);
			response.setContentType("text/plain");
			response.getWriter().write(res);
		}
	}
	
	public static String generateHash(String h_url) {
		
		Random r = new Random();
		String tmp = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String h_val = "";
		
		for (int i=0;i<20;i++) {
			h_val += tmp.charAt(r.nextInt(tmp.length()));
		}
		
		return h_val;
	}
	
	public String createTable() {

		String result = "Table Created ..";
		
		conn = DBConnection.getDBConnection();
		System.out.println(" Connection Established successfully >>>>>>>>> " + conn);
		
		//create the tables here
		try {
			PreparedStatement pstmt = conn.prepareStatement(CREATE_TABLE_QUERY);
			pstmt.executeUpdate();
			System.out.println("Created emp table >>>>>>>>>>>> ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in creating the emp tables..");
			e.printStackTrace();
		}
		
		DBConnection.closeDBConnection(conn);
		return result;
	}
	
	public String insertIntoTable(String h_value, String h_url) {
		
		String result = "Hash Value is : " + h_value;
		conn = DBConnection.getDBConnection();
		System.out.println(" Connection Established successfully >>>>>>>>> ");
		try {
			PreparedStatement pstmt = conn.prepareStatement(INSERT_TABLE_QUERY);
			pstmt.setString(1, h_value);
			pstmt.setString(2, h_url);
			pstmt.executeUpdate();
			System.out.println("inserted data into emp table >>>>>>>>>>>> ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in inserting into emp table...");
			e.printStackTrace();
		}
		DBConnection.closeDBConnection(conn);
		return result;
	}
	
	public String deleteEntryFromTable(String h_val) {
		
		String result = "Deleted Entry ..";
		
		conn = DBConnection.getDBConnection();
		
		System.out.println("Database connection obtained from the postgres driver");
		//delete code to create table
		try {
			PreparedStatement pstmt = DBConnection.getDBConnection().prepareStatement(DELETE_FROM_TABLE_QUERY);
			pstmt.setString(1, h_val);
			int res = pstmt.executeUpdate();
			System.out.println("deleted entry from the binding table >>>>>>>>>>>> ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in deleting entry from binding table ........... ");
			e.printStackTrace();
		}
		
		DBConnection.closeDBConnection(conn);
		
		return result;
	}
	
	public String showData() {
		
		String result = "Data : \n";
		
		conn = DBConnection.getDBConnection();
		System.out.println("Database connection obtained from the postgres driver");
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SHOW_DATA_FROM_TABLE_QUERY);
			while(rs.next()){
				String h_value = rs.getString("h_value");
				String h_url = rs.getString("h_url");
				
				result += " h_value >> " + h_value + " h_url >> " + h_url + "\n";
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in getting the data from emp table >>>>>>> ");
			e.printStackTrace();
		}
		
		DBConnection.closeDBConnection(conn);
		
		return result;
	}
	
	public String getUrl(String h_value) {
		
		String result = "";
		
		conn = DBConnection.getDBConnection();
		System.out.println("Database connection obtained from the postgres driver");
		try {
			PreparedStatement pstmt = DBConnection.getDBConnection().prepareStatement(GET_URL_QUERY);
			pstmt.setString(1, h_value);
			ResultSet rs = pstmt.executeQuery();
			while(rs.next()){
				String h_url = rs.getString("h_url");
				
				result += h_url;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in getting the data from emp table >>>>>>> ");
			e.printStackTrace();
		}
		
		DBConnection.closeDBConnection(conn);
		
		return result;
	}
	
	public String deleteTable() {
		
		String result = " Table Deleted ";
		
		conn = DBConnection.getDBConnection();
		
		//delete instance table
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(DELETE_TABLE_QUERY);
			System.out.println("Droped emp table >>>>>>>>>>>> ");
		} catch (SQLException e) {
			System.out.println("Error in droping the emp tables..");
			e.printStackTrace();
		}
		
		DBConnection.closeDBConnection(conn);
		
		return result;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
