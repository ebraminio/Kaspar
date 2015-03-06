package mediawiki.request.wikibase;

import javat.xml.Document;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.wikibase.Property;


public class HasClaimRequest extends WikimediaRequest {

	public HasClaimRequest(String entity) {
		setProperty("entity", entity);
	}
	
	public HasClaimRequest(String entity, Property property) {
		setProperty("entity", entity);
		setProperty("property", property.toString());
	}

	@Override
	public Boolean request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetclaims");
		Document d = p.requestDocument();
		return d.getRootElement().getChildren("claims").get(0).getChildren("property").size() >0;
	}

}
