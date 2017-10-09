package main.com.twitterStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.com.sampleService.model.DBConnection;
import twitter4j.FilterQuery;
import twitter4j.HashtagEntity;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class StreamTwitterData {
	
	public static final String TABLE_NAME = "data";
	public static final String INSERT_TABLE_QUERY = "Insert into " + TABLE_NAME
			+ " (user_handle, tweet_data, hash_tag) values "
			+ " (?, ?, ?)";
	public static final String CREATE_TABLE_QUERY = "create table " + TABLE_NAME 
			+ " (id serial primary key, user_handle text, tweet_data text, hash_tag text) ";
	
	
	public static void main(String[] args) throws TwitterException {
		
		System.out.println("Inside main >>>>>>>>>>>>>>>>>>>> ");
		
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
        .setOAuthConsumerKey("3jmA1BqasLHfItBXj3KnAIGFB")
        .setOAuthConsumerSecret("imyEeVTctFZuK62QHmL1I0AUAMudg5HKJDfkx0oR7oFbFinbvA")
        .setOAuthAccessToken("265857263-pF1DRxgIcxUbxEEFtLwLODPzD3aMl6d4zOKlMnme")
        .setOAuthAccessTokenSecret("uUFoOOGeNJfOYD3atlcmPtaxxniXxQzAU4ESJLopA1lbC");
         TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
                 .getInstance();
         
         System.out.println("Got twitter stream >>>>>>>>>>>>>>>>> ");
         
       StatusListener listener = new StatusListener() {
    	   
           @Override
           public void onStatus(Status status) {
        	   System.out.println("Inside on status >>>>>>>>>>>>> ");
               System.out.println("@" + status.getUser().getScreenName() + " ---> " + status.getText());
               //This method is used to send data to postgres.
               try {
            	   pushTweetsToPostgres(status);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
           }

           @Override
           public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
               System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
           }

           @Override
           public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
               System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
           }

           @Override
           public void onScrubGeo(long userId, long upToStatusId) {
               System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
           }

           @Override
           public void onStallWarning(StallWarning warning) {
               System.out.println("Got stall warning:" + warning);
           }
           
           @Override
           public void onException(Exception ex) {
               ex.printStackTrace();
           }
       };
       
       System.out.println(" Before AddListener >>>>>>>>>>>>>>>>>>>> ");
       
       twitterStream.addListener(listener);
       
       System.out.println(" Listener Added >>>>>>>>>>>>>>>>>>>> ");
       //twitterStream.sample();
       FilterQuery filtre = new FilterQuery();
       
       System.out.println(" Filter created >>>>>>>>>>>>>>>>>>>>>>> ");
       //String[] keywordsArray = { "#cfsummit" };
       String[] keywordsArray = {"#cfdemo", "#cfsummit"};
       filtre.track(keywordsArray);       
       System.out.println(" Added filter track >>>>>>>>>>>>>>>>>>>> ");
       twitterStream.filter(filtre);
       
       System.out.println("End Filter >>>>>>>>>>>>>>>>>>>>>>>>>>> ");
       
   }
	
	public static boolean tableExist(Connection conn, String tableName) throws SQLException {
	    boolean tExists = false;
	    ResultSet rs = conn.getMetaData().getTables(null, null, tableName, new String[] {"TABLE"});
        while (rs.next()) { 
            String tName = rs.getString("TABLE_NAME");
            System.out.println("table name >>> " + tName);
            if (tName != null && tName.equals(tableName.toLowerCase())) {
                tExists = true;
                break;
            }
        }
	    return tExists;
	}
	
	public static void createTable(Connection conn) {
		
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
	
	public static void pushTweetsToPostgres(Status status) throws SQLException {
		
		System.out.println("Inside push data >>>>>>>>>>>>>>> ");
		Connection conn = DBConnection.getDBConnection();
		
		if (null != conn) {
			
			boolean flag = tableExist(conn, TABLE_NAME);
			
			if (!flag) {
				//Table not present create table
				createTable(conn);
			}
			
			HashtagEntity[] ht = status.getHashtagEntities();
			for (HashtagEntity h : ht) {
				try {
					PreparedStatement pstmt = conn.prepareStatement(INSERT_TABLE_QUERY);
					
					String userHandle = status.getUser().getScreenName();
					String tweetData = status.getText();
					String hashTag = h.getText();
					
					//System.out.println(userHandle + " --> " + tweetData + " --> " + hashTag);
					
					pstmt.setString(1, userHandle);
					pstmt.setString(2, tweetData);
					pstmt.setString(3, hashTag);
					
					pstmt.executeUpdate();
					System.out.println("inserted tweets >>>>>>>>>>>> ");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("Error in inserting tweets....");
					e.printStackTrace();
				}
			}
			
			DBConnection.closeDBConnection(conn);
		} else {
			System.out.println(" Could not connect !! >>>> ");
		}
	}
}
