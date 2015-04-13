package main;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;

import mediawiki.WikimediaConnection;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.LoginRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;

public class GNDLoadGND {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String username = Config.USERNAME;
		String password = Config.PASSWORD;
		
		WikimediaConnection wikidata = new WikimediaConnection("www","wikidata.org");
		wikidata.request(new LoginRequest(username, password));
		wikidata.setBot(true);
		
		Connection connect = null;
		PreparedStatement preparedStatement = null;
		  
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://localhost/kasparbot?user=kasparbot&password="+password);
		
		Statement st = connect.createStatement();
		Statement st2 = connect.createStatement(); 
		
		preparedStatement = connect.prepareStatement("UPDATE gnddata SET gnd = ? WHERE wikibase = ?");
		
		int i = 0;
		
		SimpleDateFormat log = new SimpleDateFormat("HH:mm:ss");
		
		
		ResultSet r = st.executeQuery("SELECT * FROM gnddata WHERE gnd IS NULL ORDER BY ID DESC");
		while(r.next()){
			try{
				ArrayList<mediawiki.info.wikibase.Statement> arl = ((ArrayList<mediawiki.info.wikibase.Statement>)wikidata.request(new GetSpecificStatementRequest(r.getString("wikibase"), new Property(227))));
				if(arl.size() == 0){
					st2.executeUpdate("DELETE FROM gnddata WHERE wikibase = '"+r.getString("wikibase")+"' AND ID = "+r.getInt("ID")+" LIMIT 1");
					System.err.println(r.getString("wikibase")+" without GND deleted");
					continue;
				}
				StringSnak s = (StringSnak) arl.get(0).getClaim().getSnak();
				preparedStatement.setString(1, s.getValue());
				preparedStatement.setString(2, r.getString("wikibase"));
				preparedStatement.executeUpdate();
				
				if(i % 500 == 0){
					System.out.println("["+log.format(new Date())+"] "+i);
				}
				i++;
			}catch(Exception e){e.printStackTrace(); continue; }
		}
		
	}

}
