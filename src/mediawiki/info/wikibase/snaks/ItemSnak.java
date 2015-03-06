package mediawiki.info.wikibase.snaks;

import javat.xml.Element;

import mediawiki.info.wikibase.Snak;

import org.json.JSONException;
import org.json.JSONObject;


public class ItemSnak extends Snak<Integer> {

	
	public ItemSnak(Integer value) {
		super(value);
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject value = new JSONObject();
		value.put("entity-type", "item");
		value.put("numeric-id", getID());
		return value;
	}

	public int getID() {
		return getValue();
	}


	public void setID(int id) {
		setValue(id);
	}

	@Override
	public boolean equalsXML(Element e) {
		try{
			e = e.getChildren("value").get(0);
		}catch(IndexOutOfBoundsException | NullPointerException e2){
			return false;
		}
		if(! e.getAttribute("entity-type").getValue().equals("item"))
			return false;
		if(! e.getAttribute("numeric-id").getValue().equals(""+getID()))
			return false;
		return true;
	}

	@Override
	public void convert(Element element) { // ab datavalue
		setID(Integer.parseInt(element.getChildren("value").get(0).getAttribute("numeric-id").getValue()));
	}

	@Override
	public JSONObject toReferenceRepresentation() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("type", "wikibase-entityid");
		o.put("value", toJSONObject());
		return o;
	}

	@Override
	public JSONObject toClaimRepresentation() throws JSONException {
		return toJSONObject();
	}

}
