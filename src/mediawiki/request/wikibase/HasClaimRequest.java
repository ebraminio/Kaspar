package mediawiki.request.wikibase;

import javat.xml.Document;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.wikibase.Property;


public class HasClaimRequest extends MediaWikiRequest<Boolean> {

	public HasClaimRequest(String entity) {
		setProperty("entity", entity);
	}
	
	public HasClaimRequest(String entity, Property property) {
		setProperty("entity", entity);
		setProperty("property", property.toString());
	}

	@Override
	public Boolean request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetclaims");
		Document d = p.requestDocument();
		return d.getRootElement().getChildren("claims").get(0).getChildren("property").size() >0;
	}

}
