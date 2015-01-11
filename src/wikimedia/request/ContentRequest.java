package wikimedia.request;

import java.io.IOException;

import javat.xml.Document;
import javat.xml.Element;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import wikimedia.WikimediaConnection;
import wikimedia.WikimediaPostRequest;
import wikimedia.WikimediaRequest;

public class ContentRequest extends WikimediaRequest {

	
	public ContentRequest(String titles) {
		setProperty("titles",titles);
	}

	@Override
	public Object request(WikimediaConnection c) throws IOException,
			XMLStreamException, SAXException {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("continue", "");
		p.putData("prop", "revisions");
		p.putData("format", "xml");
		p.putData("rvlimit", "1");
		p.putData("rvprop", "content");
		Document d = p.requestDocument();
		Element e = d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("revisions").get(0).getChildren("rev").get(0);
		return e.getText();
	}

}
