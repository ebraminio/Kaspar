package mediawiki.task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import javat.xml.Element;
import datasets.in.GND;
import datasets.in.MARC;
import mediawiki.WikidataQuery;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaTask;
import mediawiki.WikimediaUtil;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.WikibaseDate;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.request.wikibase.AddQualifierRequest;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.GetLabelRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.HasClaimRequest;
import mediawiki.request.wikibase.SetLabelRequest;
import mediawiki.request.wikibase.SetReferenceRequest;

public class GNDSetInformationTask extends WikimediaTask {

	public GNDSetInformationTask(WikimediaConnection con) {
		super(con);
	}

	@Override
	public void run() {
		try{
			WikimediaConnection wikidata = getConnection();
			
			
			SimpleDateFormat log = new SimpleDateFormat("HH:mm:ss");
			  
			WikidataQuery wdq = new WikidataQuery("CLAIM[31:5] AND CLAIM[227]");
			
			List<Integer> l = wdq.request();
			
			Collections.shuffle(l);
			
			System.out.println(l.size()+" Einträge geladen");
			for(Integer b : l){
				if(isStopped())
					return;
				String wikibase = "Q"+b;
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
						Statement s = WikimediaUtil.addTrustedStatement(wikidata, wikibase, new Claim(new Property(569), new DateSnak(GND.parseWikibaseDate(e.getChildren("dateOfBirth").get(0).getText()))), ref);
						if(isGeborenCa(marc) && s != null){
							getConnection().request(new AddQualifierRequest(s, new Claim(1480,5727902)));
							System.out.println(Thread.currentThread().getName()+"\t["+log.format(new Date())+"]\tCirca-Qualifier created");
						}
					}
					if(e.getChildren("dateOfDeath").size() > 0){
						marc = (marc != null ? marc : GND.getMARCEntry(gnd));
						Statement s = WikimediaUtil.addTrustedStatement(wikidata, wikibase, new Claim(new Property(570), new DateSnak(GND.parseWikibaseDate(e.getChildren("dateOfDeath").get(0).getText()))), ref);
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
							WikimediaUtil.addTrustedStatement(wikidata, wikibase, claim, ref);
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
						handleComplexImport(wikidata, wikibase, e, "placeOfBirth", 19, ref, "place", "CLAIM[131]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					try{
						handleComplexImport(wikidata,  wikibase, e, "placeOfDeath", 20, ref, "place", "CLAIM[131]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					try{
						handleComplexImport(wikidata,  wikibase, e, "placeOfActivity", 937, ref, "place", "CLAIM[131]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					try{
						handleComplexImport(wikidata, wikibase, e, "professionOrOccupation", 106, ref, "occupation", "claim[31:(TREE[13516667][][279])]");
					}catch(Exception exception){exception.printStackTrace(); continue;}
					if(e.getChildren("academicDegree").size() > 0){
						switch(e.getChildren("academicDegree").get(0).getText()){
						case "Doktor":
						case "Dr." : 
							WikimediaUtil.addTrustedStatement(wikidata, wikibase, new Claim(512,849697), ref);
							break;
						case "Dr. iur.":
						case "Dr. jur.":
							WikimediaUtil.addTrustedStatement(wikidata, wikibase, new Claim(512,959320), ref);
							break;
						case "Dr. med":
						case "Dr. med.":
							WikimediaUtil.addTrustedStatement(wikidata, wikibase, new Claim(512,913404), ref);
							break;
						case "Prof.":
						case "Prof":
						case "Professor":
						case "Professorin":
							WikimediaUtil.addTrustedStatement(wikidata, wikibase, new Claim(106,121594), ref);
							break; 
						case "Prof. Dr.":
						case "Prof., Dr.":
						case "Dr., Professor":
						case "Prof.Dr.":
							WikimediaUtil.addTrustedStatement(wikidata, wikibase, new Claim(106,121594), ref);
							WikimediaUtil.addTrustedStatement(wikidata, wikibase, new Claim(512,849697), ref);
							break;
						case "Graf":
							WikimediaUtil.addTrustedStatement(wikidata, wikibase, new Claim(97,28989), ref);
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
	
	private static void handleComplexImport(WikimediaConnection wikidata, String base, Element e, String tag, int prop, Reference ref, String referer, String condition) throws IOException, JSONException, Exception{
		if(! (e.getChildren(tag).size() > 0 && e.getChildren(tag).get(0).getChildren("Description").size() > 0))
			return;
		ArrayList<Claim> cs = new ArrayList<>();
		for(Element e2 : e.getChildren(tag)){
			String gnd = e2.getChildren("Description").get(0).getAttribute("about").getValue().substring("http://d-nb.info/gnd/".length());
			WikidataQuery q = new WikidataQuery("STRING[227:\""+gnd+"\"] AND ("+condition+")");
			List<Integer> wqresult = q.request(); 
			if(wqresult.size() == 1){
				int place = wqresult.get(0);
				cs.add(new Claim(prop,place));
			}
		}
		if(wikidata.request(new HasClaimRequest(base, new Property(prop))) ){
			for(Claim c : cs){
				WikimediaUtil.addTrustedStatement(wikidata, base, c, ref);
				System.out.println("created or referenced: "+c);
			}
		}else{
			for(Claim c : cs){
				Statement s = wikidata.request(new CreateClaimRequest(base, c));
				wikidata.request(new SetReferenceRequest(s, ref));
				System.out.println("created and referenced: "+c);
			}
		}
	}
}
