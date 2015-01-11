package wikimedia.request.wikibase;

import java.io.IOException;
import java.io.ObjectOutputStream.PutField;
import java.util.ArrayList;

import javat.xml.Document;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import wikimedia.WikimediaConnection;
import wikimedia.WikimediaPostRequest;
import wikimedia.WikimediaRequest;
import wikimedia.info.Article;

public class HasClaimRequest extends WikimediaRequest {

	public HasClaimRequest(String entity) {
		setProperty("entity", entity);
	}
	
	public HasClaimRequest(String entity, String property) {
		setProperty("entity", entity);
		setProperty("property", property);
	}

	@Override
	public Boolean request(WikimediaConnection c) throws IOException,
			XMLStreamException, SAXException {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetclaims");
		Document d = p.requestDocument();
		return d.getRootElement().getChildren("claims").get(0).getChildren("property").size() >0;
	}

}
