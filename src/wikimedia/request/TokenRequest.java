package wikimedia.request;

import java.io.IOException;

import javat.xml.Element;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import wikimedia.WikimediaConnection;
import wikimedia.WikimediaPostRequest;
import wikimedia.WikimediaRequest;

public class TokenRequest extends WikimediaRequest {

	private String type = "csrf";
	
	public TokenRequest() {
		// TODO Auto-generated constructor stub
	}
	
	public TokenRequest(String type) {
		setType(type);
	}

	@Override
	public String request(WikimediaConnection c) throws IOException,
			XMLStreamException, SAXException {
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
