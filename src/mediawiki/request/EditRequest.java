package mediawiki.request;

import javat.xml.Document;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.Article;


public class EditRequest extends MediaWikiRequest<String> implements ManipulativeRequest {

	public EditRequest(Article a, String text, String summary) {
		setProperty("pageid", a.getPageid());
		setProperty("text", text);
		setProperty("summary", summary);
	}

	@Deprecated
	public EditRequest(String string, String text, String summary) {
		setProperty("title", string);
		setProperty("text", text);
		setProperty("summary", summary);
	}
	
	

	@Override
	public String request(MediaWikiConnection c) throws Exception {
		String token =  c.request(new TokenRequest());
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "edit");
		p.putData("token", token);
		Document d = p.requestDocument();
		return d.getRootElement().getChildren("edit").get(0).getAttribute("result").getValue();
	}
}
