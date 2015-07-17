package mediawiki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class WikidataQuery {

	public static final String API_URL = "https://wdq.wmflabs.org/api";
	
	private String query;
	private String apiurl;
	
	public WikidataQuery(String query){
		this(query,API_URL);
	}
	
	public WikidataQuery(String query, String apiurl){
		setQuery(query);
		setApiUrl(apiurl);
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getApiUrl() {
		return apiurl;
	}

	public void setApiUrl(String apiurl) {
		this.apiurl = apiurl;
	}
	
	public List<Integer> request() throws IOException, JSONException{
		JSONObject o = null;
		try{
			SimplePostRequest p = new SimplePostRequest(getApiUrl());
			p.putData("q", getQuery());
			String r = p.request();
			o = new JSONObject(r);
			if(!o.getJSONObject("status").getString("error").equals("OK")){
				throw new MediaWikiException("WMFLABS-Server Message: "+o.getJSONObject("status").getString("error"));
			}
		}catch(IOException io){
			throw new IOException(io.getMessage()+" for query: "+query, io);
		}
		ArrayList<Integer> result = new ArrayList<>();
		for(int i = 0; i < o.getJSONArray("items").length(); i++)
			result.add(o.getJSONArray("items").getInt(i));
		return result;
	}
	
	public int requestCount() throws JSONException, IOException {
		PostRequest p = new PostRequest(getApiUrl());
		p.putData("q", getQuery());
		p.putData("noitems", "1");
		String r = p.request();
		JSONObject o = new JSONObject(r);
		if(!o.getJSONObject("status").getString("error").equals("OK")){
			throw new MediaWikiException("WMFLABS-Server Message: "+o.getJSONObject("status").getString("error"));
		}
		return o.getJSONObject("status").getInt("items");
	}
	
}
