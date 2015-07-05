package mediawiki.request;

import java.util.HashMap;
import java.util.Map;

import javat.xml.Attribute;
import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;

public class PageInfoRequest extends WikimediaRequest<Map<String, String>> {

	public PageInfoRequest(String title){
		setProperty("titles", title);
	}
	
	@Override
	public Map<String, String> request(WikimediaConnection c)
			throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("prop", "info");
		Element e = p.requestDocument().getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0);
		if(e.getAttribute("missing") != null)
			return null;
		HashMap<String,String> h = new HashMap<>();
		for(Attribute a : e.getAttributes())
			h.put(a.getName(),a.getValue());
		return h;
	}

}
