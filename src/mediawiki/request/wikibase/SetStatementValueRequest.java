package mediawiki.request.wikibase;


import org.json.JSONException;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.wikibase.Snak;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class SetStatementValueRequest extends MediaWikiRequest<Object> implements ManipulativeRequest {

	public SetStatementValueRequest(Statement s, Snak<?> c, String summary) throws JSONException{
		setProperty("claim", s.getId());
		setProperty("value", c.toClaimRepresentation());
		setProperty("snaktype", "value");
		setProperty("summary", summary);
	}
	
	@Override
	public Object request(MediaWikiConnection c) throws Exception {
		String token = c.request(new TokenRequest());
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetclaimvalue");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
