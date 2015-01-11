package wikimedia.request.wikibase;

import java.io.IOException;
import java.io.ObjectOutputStream.PutField;

import javat.xml.Document;

import javax.xml.stream.XMLStreamException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import wikimedia.WikimediaConnection;
import wikimedia.WikimediaPostRequest;
import wikimedia.WikimediaRequest;
import wikimedia.info.wikibase.Value;
import wikimedia.request.TokenRequest;

public class SetReferenceRequest extends WikimediaRequest {

	public SetReferenceRequest(String id, String prop, Value v) throws JSONException {
		setProperty("statement", id);
		JSONObject snaks = new JSONObject();
		JSONObject value = new JSONObject();
		value.put("snaktype","value");
		value.put("property",prop);
		value.put("datavalue",v.toJSONObject());
		JSONArray array = new JSONArray();
		array.put(value);
		snaks.put(prop, array);
		setProperty("snaks", snaks.toString());
	}

	@Override
	public Object request(WikimediaConnection c) throws IOException,
			XMLStreamException, SAXException {
		String token = (String) c.request(new TokenRequest("csrf"));
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetreference");
		p.putData("token", token);
		Document d = p.requestDocument();
		return null;
	}

}
