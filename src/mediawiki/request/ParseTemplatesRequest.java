package mediawiki.request;

import java.io.ByteArrayInputStream;
import javat.xml.Document;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;


public class ParseTemplatesRequest extends WikimediaRequest {

	public ParseTemplatesRequest(String wikitext) {
		setProperty("text", wikitext);
	}
	
	public ParseTemplatesRequest(String wikitext, String title) {
		this(wikitext);
		setProperty("title", title);		
	}
	
	@Override
	public Document request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "expandtemplates");
		p.putData("includecomments","0");
		p.putData("prop","parsetree");
		Document d = p.requestDocument();
		String t = d.getRootElement().getChildren("expandtemplates").get(0).getChildren("parsetree").get(0).getText();
		Document d2 = Document.load(new ByteArrayInputStream(t.getBytes()));
		return d2;
	}

}
