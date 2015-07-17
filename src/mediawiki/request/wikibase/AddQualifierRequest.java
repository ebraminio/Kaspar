package mediawiki.request.wikibase;

import org.json.JSONException;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class AddQualifierRequest extends MediaWikiRequest implements ManipulativeRequest{

	public AddQualifierRequest(Statement target, Claim qualifier) throws JSONException{
		setProperty("claim", target.getId());
		setProperty("property", qualifier.getProperty().toString());
		setProperty("value", qualifier.getSnak().toClaimRepresentation().toString());
		setProperty("snaktype", "value");
	}
	
	@Override
	public Object request(MediaWikiConnection c) throws Exception {
		String token = (String) c.request(new TokenRequest());
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetqualifier");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
