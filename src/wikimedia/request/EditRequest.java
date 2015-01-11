package wikimedia.request;

import java.io.IOException;

import javat.xml.Document;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import wikimedia.WikimediaConnection;
import wikimedia.WikimediaPostRequest;
import wikimedia.WikimediaRequest;

public class EditRequest extends WikimediaRequest {

	private String page;
	private String text;
	private String summary;
	
	public EditRequest(String page, String text, String summary) {
		setPage(page);
		setText(text);
		setSummary(summary);
	}

	@Override
	public String request(WikimediaConnection c) throws IOException,
			XMLStreamException, SAXException {
		String token = (String) c.request(new TokenRequest("csrf"));
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData("action", "edit");
		p.putData("title", getPage());
		p.putData("text", getText());
		p.putData("token", token);
		p.putData("summary",getSummary());
		Document d = p.requestDocument();
		return d.getRootElement().getChildren("edit").get(0).getAttribute("result").getValue();
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
