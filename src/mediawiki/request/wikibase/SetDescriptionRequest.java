package mediawiki.request.wikibase;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;


public class SetDescriptionRequest extends MediaWikiRequest implements ManipulativeRequest {

	public SetDescriptionRequest(String entity, String language, String description ){
		this(entity,language,description,"");
	}
	
	public SetDescriptionRequest(String entity, String language, String description, String summary ){
		setProperty("language", language);
		setProperty("value", description);
		setProperty("id",(! entity.startsWith("Q") ? "Q" : "")+entity);
		setProperty("summary",summary);
	}
	
	@Override
	public Object request(MediaWikiConnection c) throws Exception {
		String token = c.request(new TokenRequest());
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetdescription");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
