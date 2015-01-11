package wikimedia.request.wikibase;

import java.io.IOException;
import java.io.ObjectOutputStream.PutField;

import javat.xml.Document;

import javax.xml.stream.XMLStreamException;

import org.json.JSONObject;
import org.xml.sax.SAXException;

import wikimedia.WikimediaConnection;
import wikimedia.WikimediaPostRequest;
import wikimedia.WikimediaRequest;
import wikimedia.info.wikibase.Claim;
import wikimedia.request.TokenRequest;

public class AddClaimRequest extends WikimediaRequest {

	public AddClaimRequest(String entity, String prop, Object value) {
		setProperty("entity", entity);
		setProperty("property", prop);
		setProperty("value", value.toString());
		setProperty("snaktype", "value");
	}
	
	public AddClaimRequest(String entity, String prop, String value){
		this(entity,prop,(Object)value);
	}
	
	public AddClaimRequest(String entity, String prop, JSONObject value){
		this(entity,prop,(Object)value.toString());
	}
	
	

	@Override
	public Claim request(WikimediaConnection c) throws IOException,
			XMLStreamException, SAXException {
		String token = (String) c.request(new TokenRequest("csrf"));
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbcreateclaim");
		p.putData("token", token);
		Document d = p.requestDocument();
		return Claim.convert(d.getRootElement().getChildren("claim").get(0));
	}

}
