package wikimedia.info.wikibase;

import org.json.JSONException;
import org.json.JSONObject;

public class EntityValue extends Value {

	private int id;
	
	
	public EntityValue(int id){
		this.id = id;
	}


	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("type", "wikibase-entityid");
		JSONObject o2 = new JSONObject();
		o2.put("entity-type", "item");
		o2.put("numeric-id", id);
		o.put("value", o2);
		return o;
	}
	
	
	
	
}
