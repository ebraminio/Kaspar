package mediawiki.request;

import javat.xml.Document;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.Article;


public class EditRequest extends WikimediaRequest<String> implements ManipulativeRequest {

	public EditRequest(Article a, String text, String summary) {
		setProperty("pageid", a.getPageid());
		setProperty("text", text);
		setProperty("summary", summary);
	}

	public EditRequest(String string, String text, String summary) {
		setProperty("title", string);
		setProperty("text", text);
		setProperty("summary", summary);
	}
	
	

	@Override
	public String request(WikimediaConnection c) throws Exception {
		String token =  c.request(new TokenRequest());
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData("action", "edit");
		p.putData("token", token);
		Document d = p.requestDocument();
		return d.getRootElement().getChildren("edit").get(0).getAttribute("result").getValue();
	}
}
