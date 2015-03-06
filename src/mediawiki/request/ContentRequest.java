package mediawiki.request;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;


public class ContentRequest extends WikimediaRequest {

	
	public ContentRequest(String titles) {
		setProperty("titles",titles);
	}

	@Override
	public Object request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("prop", "revisions");
		p.putData("format", "xml");
		p.putData("rvprop", "content");
		Document d = p.requestDocument();
		Element e = d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("revisions").get(0).getChildren("rev").get(0);
		return e.getText();
	}

}
