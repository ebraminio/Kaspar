package mediawiki.request.wikibase;

import javat.xml.Document;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Snak;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

import org.json.JSONException;


public class CreateClaimRequest extends MediaWikiRequest<Statement>  implements ManipulativeRequest{

	public CreateClaimRequest(String entity, Property prop, Snak<?> value) throws JSONException {
		setProperty("entity", entity);
		setProperty("property", prop.toString());
		setProperty("value", value.toClaimRepresentation());
		setProperty("snaktype", "value");
	}
	
	public CreateClaimRequest(String entity, Claim a) throws JSONException {
		this(entity, a.getProperty(), a.getSnak());
	}
	
	

	@Override
	public Statement request(MediaWikiConnection c) throws Exception {
		String token = (String) c.request(new TokenRequest("csrf"));
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbcreateclaim");
		p.putData("token", token);
		Document d = p.requestDocument();
		return new Statement(d.getRootElement().getChildren("claim").get(0));
	}

}
