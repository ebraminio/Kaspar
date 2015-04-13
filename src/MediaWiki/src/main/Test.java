package main;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import mediawiki.WikimediaConnection;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.GetTemplateValuesRequest;
import mediawiki.request.LoginRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.HasClaimRequest;
import mediawiki.request.wikibase.SetReferenceRequest;


public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		WikimediaConnection wikipedia = new WikimediaConnection("de","wikipedia.org");
		
		String username = Config.USERNAME;
		String password = Config.PASSWORD;
		
		WikimediaConnection wikidata = new WikimediaConnection("www","wikidata.org");
		wikidata.request(new LoginRequest(username, password));
		wikidata.setBot(true);
		
		String template = "Infobox Museum";
		
		Scanner s = new Scanner(new File("/home/tim/Arbeitsfläche/museen.txt"));
		String line;
		while( (line = s.nextLine()) != null){
			HashMap<String, String> r = (HashMap<String, String>) wikipedia.request(new GetTemplateValuesRequest(line, template));
			if(r.get("ISIL") != null && !r.get("ISIL").equals("")){
				System.out.print(line+"\t");
				String base;
				try{
				base = (String )wikipedia.request(new WikiBaseItemRequest(line));
				}catch(NullPointerException e){
					continue;
				}
				Boolean b = (Boolean) wikidata.request(new HasClaimRequest(base,new Property(791)));
				if(!b){
					Statement c = (Statement) wikidata.request(new CreateClaimRequest(base, new Property(791), new StringSnak(r.get("ISIL"))));
					
					wikidata.request(new SetReferenceRequest(c, new Claim(143, 48183)));
				}
				System.out.print(base+"\t");
				
				System.out.println(r.get("ISIL"));
			}
			
		}
		
	}

}
