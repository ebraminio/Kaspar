package datasets.in;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import util.GetRequest;

public class VIAF {

	public static JSONObject getLinks(String viaf) throws IOException, JSONException{
		GetRequest g = new GetRequest("https://viaf.org/viaf/"+viaf+"/justlinks.json");
		String s = g.request();
		System.out.println(s);
		return new JSONObject(s);
	}
}
