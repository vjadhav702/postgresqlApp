package main.com.twitterStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import main.com.sampleService.model.DBConnection;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class StreamTwitterData {
	
	public static final String INSERT_TABLE_QUERY = "Insert into tweet_Table "
			+ " (user_handle, tweet_data) values "
			+ " (?, ?)";
	
	public static void main(String[] args) throws TwitterException {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
        .setOAuthConsumerKey("3jmA1BqasLHfItBXj3KnAIGFB")
        .setOAuthConsumerSecret("imyEeVTctFZuK62QHmL1I0AUAMudg5HKJDfkx0oR7oFbFinbvA")
        .setOAuthAccessToken("265857263-pF1DRxgIcxUbxEEFtLwLODPzD3aMl6d4zOKlMnme")
        .setOAuthAccessTokenSecret("uUFoOOGeNJfOYD3atlcmPtaxxniXxQzAU4ESJLopA1lbC");
         TwitterStream twitterStream = new TwitterStreamFactory(cb.build())
                 .getInstance();
       StatusListener listener = new StatusListener() {
           @Override
           public void onStatus(Status status) {
               System.out.println("@" + status.getUser().getScreenName() + " ---> " + status.getText());
               //This method is used to send data to postgres.
               pushTweetsToPostgres(status);
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
       twitterStream.addListener(listener);
       //twitterStream.sample();
       FilterQuery filtre = new FilterQuery();
       String[] keywordsArray = { "HDJSDJIHGDHF" };
       filtre.track(keywordsArray);       
       twitterStream.filter(filtre);
   }
	
	public static void pushTweetsToPostgres(Status status) {
		
		Connection conn = DBConnection.getDBConnection();
		
		try {
			PreparedStatement pstmt = conn.prepareStatement(INSERT_TABLE_QUERY);
			
			String userHandle = status.getUser().getScreenName();
			String tweetData = status.getText();
			
			pstmt.setString(1, userHandle);
			pstmt.setString(2, tweetData);
			pstmt.executeUpdate();
			System.out.println("inserted tweets >>>>>>>>>>>> ");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in inserting tweets....");
			e.printStackTrace();
		} 
		
		DBConnection.closeDBConnection(conn);
	}
}
