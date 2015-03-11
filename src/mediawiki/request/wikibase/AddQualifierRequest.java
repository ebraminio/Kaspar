package mediawiki.request.wikibase;

import org.json.JSONException;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class AddQualifierRequest extends WikimediaRequest implements ManipulativeRequest{

	public AddQualifierRequest(Statement target, Claim qualifier) throws JSONException{
		setProperty("claim", target.getId());
		setProperty("property", qualifier.getProperty().toString());
		setProperty("value", qualifier.getSnak().toClaimRepresentation().toString());
		setProperty("snaktype", "value");
	}
	
	@Override
	public Object request(WikimediaConnection c) throws Exception {
		String token = (String) c.request(new TokenRequest());
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetqualifier");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
