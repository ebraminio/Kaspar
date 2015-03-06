package mediawiki.info.wikibase.snaks;

import java.net.MalformedURLException;
import java.net.URL;

import javat.xml.Element;

import mediawiki.info.wikibase.Snak;

import org.json.JSONException;
import org.json.JSONObject;


public class URLSnak extends Snak<URL> {

	public URLSnak(URL value) {
		super(value);
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		return null;
	}

	@Override
	public boolean equalsXML(Element e) {
		if(! e.getAttribute("type").getValue().equals("string"))
			return false;
		if(! e.getAttribute("value").getValue().equals(getValue().toExternalForm()))
			return false;
		return true;
	}

	@Override
	public Object toReferenceRepresentation() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("type", "url");
		o.put("value", getValue().toExternalForm());
		return o;
	}

	@Override
	public String toClaimRepresentation() throws JSONException {
		return "\""+getValue().toExternalForm()+"\"";
	}

	@Override
	public void convert(Element element) throws MalformedURLException {
		setValue(new URL(element.getAttribute("value").getValue()));
	}

}
