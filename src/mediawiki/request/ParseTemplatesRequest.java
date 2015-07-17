package mediawiki.request;

import java.io.ByteArrayInputStream;
import javat.xml.Document;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;


public class ParseTemplatesRequest extends MediaWikiRequest<Document> {

	public ParseTemplatesRequest(String wikitext) {
		setProperty("text", wikitext);
	}
	
	public ParseTemplatesRequest(String wikitext, String title) {
		this(wikitext);
		setProperty("title", title);		
	}
	
	@Override
	public Document request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
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
