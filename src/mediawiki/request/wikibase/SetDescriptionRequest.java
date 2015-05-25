package mediawiki.request.wikibase;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;


public class SetDescriptionRequest extends WikimediaRequest implements ManipulativeRequest {

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
	public Object request(WikimediaConnection c) throws Exception {
		String token = c.request(new TokenRequest());
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetdescription");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
