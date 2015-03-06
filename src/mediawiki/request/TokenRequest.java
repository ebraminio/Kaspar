package mediawiki.request;

import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;


public class TokenRequest extends WikimediaRequest {

	private String type = "csrf";
	
	public TokenRequest() {
		
	}
	
	public TokenRequest(String type) {
		setType(type);
	}

	@Override
	public String request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData("action", "query");
		p.putData("meta", "tokens");
		p.putData("type", getType());
		Element e = p.requestDocument().getRootElement().getChildren("query").get(0).getChildren("tokens").get(0);
		return e.getAttribute(0).getValue();
	}
	

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
