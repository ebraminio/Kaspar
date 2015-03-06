package mediawiki.task;

import static main.GNDLoad.addClaim;
import static main.GNDLoad.importDate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javat.xml.Element;
import datasets.in.GND;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaTask;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.WikibaseDate;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.wikibase.GetLabelRequest;
import mediawiki.request.wikibase.SetLabelRequest;

import static main.GNDSetInformation.importPlace;

public class GNDSetInformationTask extends WikimediaTask {

	final int version = 9; 
	private Connection connect;
	
	public GNDSetInformationTask(WikimediaConnection con, Connection mysql) {
		super(con);
		connect = mysql;
	}

	@Override
	public void run() {
		try{
			WikimediaConnection wikidata = getConnection();
			String password = "w.OKSiCokU4Ntpd";
			
			
			SimpleDateFormat log = new SimpleDateFormat("HH:mm:ss");
			  
			
			Statement statement = connect.createStatement();
			PreparedStatement preparedStatement = connect.prepareStatement("UPDATE gnddata SET version = '"+version+"' WHERE ID = ?");
			
			ResultSet r = statement.executeQuery("SELECT * FROM gnddata WHERE (gnd IS NOT NULL AND version < "+version+") ORDER BY version ASC, ID DESC");
			while(r.next()){
				try{
					Reference ref = new Reference();
					ref.addClaim(new Claim(248, 36578));
					ref.addClaim(new Claim(new Property(813), new DateSnak(new WikibaseDate(WikibaseDate.ONE_DAY))));
					ref.addClaim(new Claim(new Property(227), new StringSnak(r.getString("gnd"))));
					
					Element e = null;
					try{
						e = GND.getGNDEntry(r.getString("gnd"));
					}catch(Exception e2){
						continue;
					}
					System.out.println(Thread.currentThread().getName()+"\t["+log.format(new Date())+"]\t"+r.getString("wikibase")+"\t"+r.getString("gnd"));
					if(e.getChildren("dateOfBirth").size() > 0){
						importDate(wikidata, e.getChildren("dateOfBirth").get(0).getText(),  r.getString("wikibase"), new Property(569), ref);
					}
					if(e.getChildren("dateOfDeath").size() > 0){
						importDate(wikidata, e.getChildren("dateOfDeath").get(0).getText(),  r.getString("wikibase"), new Property(570), ref);
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
							addClaim(wikidata, r.getString("wikibase"), claim, ref);
						}
					}
					if(e.getChildren("sameAs").size() > 0){
						for(Element viaf : e.getChildren("sameAs")){
							if(viaf.getAttribute("resource").getValue().startsWith("http://viaf.org/viaf/")){
								addClaim(wikidata,  r.getString("wikibase"), new Claim(new Property(214),new StringSnak(viaf.getAttribute("resource").getValue().substring("http://viaf.org/viaf/".length()))), ref);
							}
						}
					}
					if(e.getChildren("preferredNameEntityForThePerson").size() > 0 && e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("forename").size() == 1 && e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("surname").size() == 1){
						String name = e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("forename").get(0).getText()+" "+
								e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("surname").get(0).getText();
						name = name.replaceAll("\\- ", "-");
						if(name.indexOf(".")  == -1){
							for(String lang : new String[]{"de","en","fr"}){
								if(wikidata.request(new GetLabelRequest(lang, r.getString("wikibase"))) == null){
									wikidata.request(new SetLabelRequest(r.getString("wikibase"), lang, name));
									System.out.println("Label für "+lang+" erstellt: "+name);
								}
							}
						}
					}
					try{
						importPlace(wikidata, connect, r.getString("wikibase"), e, "placeOfBirth", 19, ref, "place", "CLAIM[131]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					try{
						importPlace(wikidata, connect,  r.getString("wikibase"), e, "placeOfDeath", 20, ref, "place", "CLAIM[131]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					try{
						importPlace(wikidata, connect,  r.getString("wikibase"), e, "placeOfActivity", 937, ref, "place", "CLAIM[131]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					try{
						importPlace(wikidata, connect,  r.getString("wikibase"), e, "professionOrOccupation", 106, ref, "occupation", "claim[31:(TREE[13516667][][279])]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					if(e.getChildren("academicDegree").size() > 0){
						switch(e.getChildren("academicDegree").get(0).getText()){
						case "Doktor":
						case "Dr." : 
							addClaim(wikidata,  r.getString("wikibase"), new Claim(512,4618975), ref);
							break;
						case "Dr. iur.":
						case "Dr. jur.":
							addClaim(wikidata,  r.getString("wikibase"), new Claim(512,959320), ref);
							break;
						case "Dr. med":
						case "Dr. med.":
							addClaim(wikidata,  r.getString("wikibase"), new Claim(512,913404), ref);
							break;
						case "Prof.":
						case "Prof":
						case "Professor":
						case "Professorin":
							addClaim(wikidata,  r.getString("wikibase"), new Claim(106,121594), ref);
							break; 
						case "Prof. Dr.":
						case "Prof., Dr.":
						case "Dr., Professor":
						case "Prof.Dr.":
							addClaim(wikidata,  r.getString("wikibase"), new Claim(106,121594), ref);
							addClaim(wikidata,  r.getString("wikibase"), new Claim(512,4618975), ref);
							break;
						case "Graf":
							addClaim(wikidata,  r.getString("wikibase"), new Claim(97,28989), ref);
							break;
						default:
							System.out.println(e.getChildren("academicDegree").get(0).getText());
						}
					}
					
			/*		if(e.getChildren("variantNameEntityForThePerson").size() > 0){
						ArrayList<String> aliases = ((TranslatedContent<ArrayList<String>>) wikidata.request(new GetAliasesRequest(r.getString("wikibase"), "de"))).get("de");
						aliases = (aliases == null ? new ArrayList<String>() : aliases);
						String title = (String)wikidata.request(new GetLabelRequest("de", r.getString("wikibase")));
						aliases.add(title);
						if(title != null){
							String title2 = title;
							title2 = title2.replaceAll("ä", "ae");
							title2 = title2.replaceAll("ö", "oe");
							title2 = title2.replaceAll("ü", "ue");
							title2 = title2.replaceAll("ß", "ss");
							aliases.add(title2);
						}
						ArrayList<String> newaliases = new ArrayList<>();
						for(Element name : e.getChildren("variantNameEntityForThePerson")){
							if(name.getAttribute("parseType") == null || !name.getAttribute("parseType").getValue().equals("Resource"))
								continue;
							String n = null;
							if(name.getChildren("forename").size() > 0 || name.getChildren("surname").size() > 0){
								n = name.getChildren("forename").get(0).getText()+" "+
										name.getChildren("surname").get(0).getText();
							}else if(name.getChildren("personalName").size() > 0){
								n = name.getChildren("personalName").get(0).getText();
							}
							if(n == null)
								continue;
							n = n.replaceAll("\\- ", "-");
							if(n.indexOf(".") != -1)
								continue;
							if(! aliases.contains(n) && ! newaliases.contains(n)){
								newaliases.add(n);
								
								String title2 = n;
								title2 = title2.replaceAll("ä", "ae");
								title2 = title2.replaceAll("ö", "oe");
								title2 = title2.replaceAll("ü", "ue");
								title2 = title2.replaceAll("ß", "ss");
								aliases.add(title2);
							}
						}
						if(newaliases.size() > 0){
							wikidata.request(new AddAliasesRequest(r.getString("wikibase"), "de", "processed by KasparBot based on GND", newaliases.toArray(new String[newaliases.size()])));
							System.out.println("Aliases für "+title+" erstellt: "+newaliases.toString());
						}
					} */
					preparedStatement.setInt(1, r.getInt("ID"));
					preparedStatement.executeUpdate();
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
