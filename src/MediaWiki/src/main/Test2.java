package main;

import java.net.URLEncoder;
import java.util.List;

import mediawiki.WikidataQuery;
import mediawiki.WikimediaConnection;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.wikibase.GetSpecificStatementRequest;

import util.GetRequest;

public class Test2 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		WikimediaConnection wikidata = new WikimediaConnection("www","wikidata.org");
	//	wikidata.request(new LoginRequest(username, password));
	//	wikidata.setBot(true);
		
		WikidataQuery w = new WikidataQuery("CLAIM[791]");
		List<Integer> b = w.request();
		
		for(int i : b){
			Statement c = ((List<Statement>) wikidata.request(new GetSpecificStatementRequest("Q"+i, new Property(791)))).get(0);
			String isil = ((StringSnak)c.getClaim().getSnak()).getValue();
			System.out.println(i+"\t"+isil);
			
			String zdb = new GetRequest("http://dispatch.opac.d-nb.de/DB=1.2/CMD?ACT=SRCHA&IKT=8529&TRM="+URLEncoder.encode(isil)).request();
			System.out.println(zdb);
			if(zdb.indexOf("<TITLE>ZDB OPAC - Sigelsuche - results/titledata</TITLE>") < 0){continue;}
			String token = "<div>Haupt-/Sitzanschrift: </div></strong></td><td valign=\"bottom\" class=\"presvalue\"><div>";
			
			String anschrift = zdb.substring(zdb.indexOf(token)+token.length());
			anschrift = anschrift.substring(0, anschrift.indexOf("</div>")).trim();
			System.out.println(anschrift);
			
		}
	}

}
