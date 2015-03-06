package mediawiki;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONizable {

	public JSONObject toJSONObject() throws JSONException;
}
