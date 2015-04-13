package mediawiki.info.wikibase.snaks;

import javat.xml.Element;

import mediawiki.info.wikibase.Snak;

import org.json.JSONException;
import org.json.JSONObject;


public class StringSnak extends Snak<String> {

	public StringSnak(String v){
		super(v);
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException {
		return null;
	}

	@Override
	public boolean equalsXML(Element e) {
		if(! e.getAttribute("type").getValue().equals("string"))
			return false;
		if(! e.getAttribute("value").getValue().equals(getValue()))
			return false;
		return true;
	}

	@Override
	public void convert(Element element) {
		setValue(element.getAttribute("value").getValue());
	}

	@Override
	public JSONObject toReferenceRepresentation() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("type", "string");
		o.put("value", getValue());
		return o;
	}

	@Override
	public String toClaimRepresentation() {
		return "\""+getValue()+"\"";
	}


}
