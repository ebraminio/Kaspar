package mediawiki.task;

import static main.GNDLoad.addClaim;
import static main.GNDLoad.importDate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

import javat.xml.Element;
import datasets.in.GND;
import datasets.in.MARC;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaTask;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.WikibaseDate;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.request.wikibase.AddQualifierRequest;
import mediawiki.request.wikibase.GetLabelRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.SetLabelRequest;

import static main.GNDSetInformation.importPlace;

public class GNDSetInformationTask extends WikimediaTask {

	final int version = 9; 
	
	public GNDSetInformationTask(WikimediaConnection con) {
		super(con);
	}

	@Override
	public void run() {
		try{
			WikimediaConnection wikidata = getConnection();
			
			
			SimpleDateFormat log = new SimpleDateFormat("HH:mm:ss");
			  
			
			Scanner scanner = new Scanner(new File("/home/kaspar/gnd_ds.csv"));
			while(scanner.hasNextLine()){
				if(isStopped())
					return;
				String wikibase = scanner.nextLine().trim();
				try{
					if(getConnection().request(new GetSpecificStatementRequest(wikibase, new Claim(31, new ItemSnak(5)))).size() == 0){
						System.err.println(wikibase+" isn't a human!");
						increaseDone();
						continue;
					}
					
					String gnd = (String) getConnection().request(new GetSpecificStatementRequest(wikibase, new Property(227))).get(0).getClaim().getSnak().getValue();
					
					Reference ref = new Reference();
					ref.addClaim(new Claim(248, 36578));
					ref.addClaim(new Claim(new Property(813), new DateSnak(new WikibaseDate(WikibaseDate.ONE_DAY))));
			//		ref.addClaim(new Claim(new Property(227), new StringSnak(gnd)));
					
					Element e = null;
					try{
						e = GND.getGNDEntry(gnd);
					}catch(Exception e2){
						continue;
					}
					System.out.println(Thread.currentThread().getName()+"\t["+log.format(new Date())+"]\t"+wikibase+"\t"+gnd);
					
					MARC marc = null;
					
					if(e.getChildren("dateOfBirth").size() > 0){
						marc = GND.getMARCEntry(gnd);
						mediawiki.info.wikibase.Statement s = importDate(wikidata, e.getChildren("dateOfBirth").get(0).getText(),  wikibase, new Property(569), ref);
						if(isGeborenCa(marc) && s != null){
							getConnection().request(new AddQualifierRequest(s, new Claim(1480,5727902)));
							System.out.println(Thread.currentThread().getName()+"\t["+log.format(new Date())+"]\tCirca-Qualifier created");
						}
					}
					if(e.getChildren("dateOfDeath").size() > 0){
						marc = (marc != null ? marc : GND.getMARCEntry(gnd));
						mediawiki.info.wikibase.Statement s = importDate(wikidata, e.getChildren("dateOfDeath").get(0).getText(),  wikibase, new Property(570), ref);
						if(isGestorbenCa(marc) && s != null) {
							getConnection().request(new AddQualifierRequest(s, new Claim(1480,5727902)));
							System.out.println(Thread.currentThread().getName()+"\t["+log.format(new Date())+"]\tCirca-Qualifier created");
						}
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
							addClaim(wikidata, wikibase, claim, ref);
						}
					}
				/*	VIAF disabled! 
				 * if(e.getChildren("sameAs").size() > 0){
						for(Element viaf : e.getChildren("sameAs")){
							if(viaf.getAttribute("resource").getValue().startsWith("http://viaf.org/viaf/")){
								addClaim(wikidata,  wikibase, new Claim(new Property(214),new StringSnak(viaf.getAttribute("resource").getValue().substring("http://viaf.org/viaf/".length()))), ref);
							}
						}
					} */
					if(e.getChildren("preferredNameEntityForThePerson").size() > 0 && e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("forename").size() == 1 && e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("surname").size() == 1){
						String name = e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("forename").get(0).getText()+" ";
						if(e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("prefix").size() > 0){
							name += e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("prefix").get(0).getText()+" ";
						}
						name +=	e.getChildren("preferredNameEntityForThePerson").get(0).getChildren("surname").get(0).getText();
						name = name.replaceAll("\\- ", "-");
						name = name.replaceAll("  ", " ");
						if(name.indexOf(".") == -1){
							for(String lang : new String[]{"de","en","fr","es","nl"}){
								if(wikidata.request(new GetLabelRequest(lang, wikibase)) == null){
									wikidata.request(new SetLabelRequest(wikibase, lang, name));
									System.out.println("Label für "+lang+" erstellt: "+name);
								}
							}
						}
					}
					try{
						importPlace(wikidata, wikibase, e, "placeOfBirth", 19, ref, "place", "CLAIM[131]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					try{
						importPlace(wikidata,  wikibase, e, "placeOfDeath", 20, ref, "place", "CLAIM[131]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					try{
						importPlace(wikidata,  wikibase, e, "placeOfActivity", 937, ref, "place", "CLAIM[131]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					try{
						importPlace(wikidata, wikibase, e, "professionOrOccupation", 106, ref, "occupation", "claim[31:(TREE[13516667][][279])]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					if(e.getChildren("academicDegree").size() > 0){
						switch(e.getChildren("academicDegree").get(0).getText()){
						case "Doktor":
						case "Dr." : 
							addClaim(wikidata,  wikibase, new Claim(512,849697), ref);
							break;
						case "Dr. iur.":
						case "Dr. jur.":
							addClaim(wikidata,  wikibase, new Claim(512,959320), ref);
							break;
						case "Dr. med":
						case "Dr. med.":
							addClaim(wikidata,  wikibase, new Claim(512,913404), ref);
							break;
						case "Prof.":
						case "Prof":
						case "Professor":
						case "Professorin":
							addClaim(wikidata,  wikibase, new Claim(106,121594), ref);
							break; 
						case "Prof. Dr.":
						case "Prof., Dr.":
						case "Dr., Professor":
						case "Prof.Dr.":
							addClaim(wikidata,  wikibase, new Claim(106,121594), ref);
							addClaim(wikidata,  wikibase, new Claim(512,849697), ref);
							break;
						case "Graf":
							addClaim(wikidata,  wikibase, new Claim(97,28989), ref);
							break;
						default:
							System.out.println(e.getChildren("academicDegree").get(0).getText());
						}
					}
					
			/*		if(e.getChildren("variantNameEntityForThePerson").size() > 0){
						ArrayList<String> aliases = ((TranslatedContent<ArrayList<String>>) wikidata.request(new GetAliasesRequest(wikibase, "de"))).get("de");
						aliases = (aliases == null ? new ArrayList<String>() : aliases);
						String title = (String)wikidata.request(new GetLabelRequest("de", wikibase));
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
							wikidata.request(new AddAliasesRequest(wikibase, "de", "processed by KasparBot based on GND", newaliases.toArray(new String[newaliases.size()])));
							System.out.println("Aliases für "+title+" erstellt: "+newaliases.toString());
						}
					} */
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}
			}
			scanner.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static boolean isGeborenCa(MARC m){
		for(HashMap<String, ArrayList<String>> f : m.getDatafield("548")){
			if(! (f.get("i").get(0).equals("Lebensdaten") || f.get("i").get(0).equals("Exakte Lebensdaten"))){
				continue;
			}
			if(f.get("9").size() <= 1)
				continue;
			String angabe = f.get("9").get(1);
			angabe = angabe.substring("v:".length());
			switch(angabe){
			case "Geburts- u. Todesjahr ca." :
			case "ca." : return true;
			case "genaues Todesdatum unbekannt":
			case "Geburtsjahr ca." : return true;
			}
			
			if(angabe.indexOf("Geburtsjahr ca.") >= 0 || angabe.indexOf("Geburts- u. Todesjahr ca.") >= 0)
				return true;
			
			if(f.get("a").get(0).matches("ca\\.[\\s\\/\\w.]+\\-[\\s\\/\\w.]+"))
				return true;
		}
		return false;
	}
	
	private static boolean isGestorbenCa(MARC m){
		for(HashMap<String, ArrayList<String>> f : m.getDatafield("548")){
			if(! (f.get("i").get(0).equals("Lebensdaten") || f.get("i").get(0).equals("Exakte Lebensdaten"))){
				continue;
			}
			if(f.get("9").size() <= 1)
				continue;
			String angabe = f.get("9").get(1);
			angabe = angabe.substring("v:".length());
			angabe = angabe.replaceAll("Todessjahr", "Todesjahr");
			
			switch(angabe){
			case "Geburts- u. Todesjahr ca." :
			case "ca." : return true;
			case "genaues Todesdatum unbekannt":
			case "Todesjahr ca." : 
			case "Sterbejahr ca." : return true;
			}
			
			if(angabe.indexOf("Sterbejahr ca.") >= 0 || angabe.indexOf("genaues Todesdatum unbekannt") >= 0 || angabe.indexOf("Todesjahr ca.") >= 0 || angabe.indexOf("Geburts- u. Todesjahr ca.") >= 0)
				return true;
			
			if(f.get("a").get(0).matches("[\\s\\/\\w.]+\\-ca\\.[\\s\\/\\w.]+"))
				return true;
		}
		return false;
	}

}
