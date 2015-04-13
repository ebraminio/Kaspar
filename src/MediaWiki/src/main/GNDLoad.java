package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Snak;
import mediawiki.info.wikibase.WikibaseDate;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.LoginRequest;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.HasClaimRequest;
import mediawiki.request.wikibase.SetReferenceRequest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import datasets.in.GND;
import datasets.out.SQLiteConnection;

public class GNDLoad {

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
		Statement statement = null;
		PreparedStatement preparedStatement = null;
		  
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://localhost/kasparbot?user=kasparbot&password="+password);
		
		statement = connect.createStatement();
		  
		preparedStatement = connect.prepareStatement("insert gnddata (wikibase, gnd) values (?, NULL)");
		Scanner s = new Scanner(new File("/home/tim/Arbeitsfläche/gnd.tsv"));
		String line;
		
		int i = 0;
		while(s.hasNextLine()){
			line = s.nextLine();
			line = line.substring(0,line.indexOf('	')).trim();
			preparedStatement.setString(1, line);
			preparedStatement.executeUpdate();
			
			if(i % 500 == 0){
				System.out.println(i);
			}
			i++;
		}
		s.close();
		
		
		
		
		
		
		
	/*	ResultSet r = sql.executeQuery("SELECT * FROM gnddata WHERE gnd = ''");
		while(r.next()){
			StringSnak s = (StringSnak) ((ArrayList<mediawiki.info.wikibase.Statement>)wikidata.request(new GetSpecificStatementRequest(r.getString("wikibase"), new Property(227)))).get(0).getClaim().getSnak();
			sql.executeUpdate("UPDATE gnddata SET gnd = '"+s.getValue()+"' WHERE wikibase = '"+r.getString("wikibase")+"'");
			System.out.println(s.getValue());
		} */ 
		
	/*	ResultSet r = sql.executeQuery("SELECT * FROM gnddata WHERE gnd != ''");
		while(r.next()){
			Element e = GND.getGNDEntry(r.getString("gnd"));
			System.out.println(r.getString("wikibase")+"\t"+r.getString("gnd"));
			if(e.getChildren("dateOfBirth").size() > 0){
				importDate(wikidata, e.getChildren("dateOfBirth").get(0).getText(),  r.getString("wikibase"), new Property(569), new Claim(143, 36578));
			}
			if(e.getChildren("dateOfDeath").size() > 0){
				importDate(wikidata, e.getChildren("dateOfDeath").get(0).getText(),  r.getString("wikibase"), new Property(570), new Claim(143, 36578));
			}
			if(e.getChildren("gender").size() > 0){
				ItemSnak i = null;
				switch(e.getChildren("gender").get(0).getAttribute("resource").getValue()){
				case "http://d-nb.info/standards/vocab/gnd/Gender#male" :
					i = new ItemSnak(6581097);
					break;
				case "http://d-nb.info/standards/vocab/gnd/Gender#female" :
					i = new ItemSnak(6581072);
					break;
				}
				if(i != null){
					Claim claim = new Claim(new Property(21),i);
					addClaim(wikidata, r.getString("wikibase"), claim, new Claim(143, 36578));
				}
			}
			if(e.getChildren("academicDegree").size() > 0){
				switch(e.getChildren("academicDegree").get(0).getText()){
				case "Dr." : 
					addClaim(wikidata,  r.getString("wikibase"), new Claim(512,4618975), new Claim(143, 36578));
					break;
				case "Dr. jur.":
					addClaim(wikidata,  r.getString("wikibase"), new Claim(512,959320), new Claim(143, 36578));
					break;
				case "Professor":
					addClaim(wikidata,  r.getString("wikibase"), new Claim(512,121594), new Claim(143, 36578));
					break;
				default:
					System.out.println(e.getChildren("academicDegree").get(0).getText());
				}
			}
		}
		*/
		
	/*	
		
		
	    statement.executeUpdate("insert into person values(2, 'yui')");
	    sql.close(); */
	}
	
	public static mediawiki.info.wikibase.Statement importDate(WikimediaConnection wikidata, String date, String base, Property p, Reference source) throws Exception{
		WikibaseDate wbd = null;
		if(date.matches("\\d\\d\\d\\d")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			wbd = new WikibaseDate(sdf.parse(date), 0, 0, 0, WikibaseDate.ONE_YEAR);
		}else if(date.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			wbd = new WikibaseDate(sdf.parse(date), 0, 0, 0, WikibaseDate.ONE_DAY);
		}else{
			return null;
		}
		
		return addClaim(wikidata, base, new Claim(p, new DateSnak(wbd)), source);
	}
	
	@Deprecated
	public static mediawiki.info.wikibase.Statement addClaim(WikimediaConnection wikidata, String entity, Claim c, Reference source) throws Exception{
		return addClaim(wikidata,entity,c,source,"processed by KasparBot based on GND");
	}
	
	public static mediawiki.info.wikibase.Statement addClaim(WikimediaConnection wikidata, String entity, Claim c, Reference source, String summary) throws Exception{
		Boolean b = (Boolean) wikidata.request(new HasClaimRequest(entity,c.getProperty()));
		SimpleDateFormat log = new SimpleDateFormat("HH:mm:ss");
		if(!b){
			mediawiki.info.wikibase.Statement s = KasparMain.addWikidataProperty(wikidata, entity, c, source, summary);
			System.out.println(Thread.currentThread().getName()+"\t["+log.format(new Date())+"]\t"+entity+" has now "+c.getProperty()+": "+c.getSnak());
			return s;
		}
		return null;
	}
		

}
