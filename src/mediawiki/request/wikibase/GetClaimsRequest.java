package mediawiki.request.wikibase;

import java.util.ArrayList;

import javat.xml.Element;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;


public class GetClaimsRequest extends MediaWikiRequest<ArrayList<Element>> {

	public GetClaimsRequest(String base) {
		setProperty("entity", base);
	}
	
	
	@Override
	public ArrayList<Element> request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetclaims");
		return p.requestDocument().getRootElement().getChildren("claims").get(0).getChildren("property");
	}

}
