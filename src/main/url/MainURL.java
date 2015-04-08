package main.url;

import java.net.URL;


import util.GetRequest;

import mediawiki.WikidataQuery;
import mediawiki.WikimediaConnection;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.LoginRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;

public class MainURL {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String username = "KasparBot";
		String password = "m#7C(}Y^40r4]/q";
		
		WikimediaConnection wikidata = new WikimediaConnection("www","wikidata.org");
		wikidata.request(new LoginRequest(username, password));
		wikidata.setBot(true);
		
		WikidataQuery wdq = new WikidataQuery("CLAIM[31:5] AND CLAIM[:] AND CLAIM[227]");
		for(Integer i : wdq.request()){
			Statement s  = wikidata.request(new GetSpecificStatementRequest("Q"+i, new Property(856))).get(0);
			GetRequest g = new GetRequest((URL) s.getClaim().getSnak().getValue());
			g.request();
		}

	}

}
