package mediawiki.request.wikibase;

import javat.xml.Document;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Snak;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SetReferenceRequest extends MediaWikiRequest implements ManipulativeRequest {

	@Deprecated
	public SetReferenceRequest(String id, Property prop, Snak<?> e) throws JSONException {
		this(new Statement(id),new Reference(prop,e));
	}
	
	@Deprecated
	public SetReferenceRequest(String id, Claim a) throws JSONException{
		this(new Statement(id),new Reference(a));
	}
	
	@Deprecated
	public SetReferenceRequest(Statement c, Claim a) throws JSONException{
		this(c, new Reference(a));
	}
	
	public SetReferenceRequest(Statement c, Reference r) throws JSONException{
		setProperty("statement", c.getId());
		JSONObject snaks = new JSONObject();
	
		
		
		for(Claim claims : r){
			JSONArray array = new JSONArray();
			JSONObject value = new JSONObject();
			value.put("snaktype","value");
			value.put("property",claims.getProperty().toString());
			value.put("datavalue",claims.getSnak().toReferenceRepresentation());
			array.put(value);
			snaks.put(claims.getProperty().toString(), array);
		}
		
		setProperty("snaks", snaks.toString());
	}

	@Override
	public Object request(MediaWikiConnection c) throws Exception {
		String token = (String) c.request(new TokenRequest("csrf"));
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetreference");
		p.putData("token", token);
		
		Document d = p.requestDocument();
		return null;
	}

}
