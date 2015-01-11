package wikimedia.request;

import java.io.IOException;

import javat.xml.Document;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import wikimedia.WikimediaConnection;
import wikimedia.WikimediaPostRequest;
import wikimedia.WikimediaRequest;
import wikimedia.info.Article;

public class WikiBaseItemRequest extends WikimediaRequest {

	public WikiBaseItemRequest(Article a) {
		setProperty("pageids", a.getPageid());
	}
	
	public WikiBaseItemRequest(String a) {
		setProperty("titles", a);
	}
	
	public WikiBaseItemRequest(int a) {
		setProperty("pageids", a);
	}

	@Override
	public Object request(WikimediaConnection c) throws IOException,
			XMLStreamException, SAXException {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("prop", "pageprops");
		Document d = p.requestDocument();
		
		return d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("pageprops").get(0).getAttribute("wikibase_item").getValue();
	}

}
