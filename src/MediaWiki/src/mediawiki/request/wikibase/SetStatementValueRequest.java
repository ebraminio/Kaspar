package mediawiki.request.wikibase;


import org.json.JSONException;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Snak;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class SetStatementValueRequest extends WikimediaRequest<Object> implements ManipulativeRequest {

	public SetStatementValueRequest(Statement s, Snak<?> c, String summary) throws JSONException{
		setProperty("claim", s.getId());
		setProperty("value", c.toClaimRepresentation());
		setProperty("snaktype", "value");
		setProperty("summary", summary);
	}
	
	@Override
	public Object request(WikimediaConnection c) throws Exception {
		String token = c.request(new TokenRequest());
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetclaimvalue");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
