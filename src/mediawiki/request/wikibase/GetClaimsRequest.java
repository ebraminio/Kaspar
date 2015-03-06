package mediawiki.request.wikibase;

import java.util.ArrayList;

import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;


public class GetClaimsRequest extends WikimediaRequest {

	public GetClaimsRequest(String base) {
		setProperty("entity", base);
	}
	
	
	@Override
	public ArrayList<Element> request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetclaims");
		return p.requestDocument().getRootElement().getChildren("claims").get(0).getChildren("property");
	}

}
