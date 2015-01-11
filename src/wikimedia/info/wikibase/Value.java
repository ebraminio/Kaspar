package wikimedia.info.wikibase;

import org.json.JSONException;
import org.json.JSONObject;

abstract public class Value {

	abstract public JSONObject toJSONObject() throws JSONException;
	
}
