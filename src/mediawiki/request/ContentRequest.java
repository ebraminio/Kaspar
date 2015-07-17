package mediawiki.request;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.Article;


public class ContentRequest extends MediaWikiRequest<String> {

	public ContentRequest(String titles) {
		setProperty("titles",titles);
	}
	
	public ContentRequest(Article a){
		setProperty("titles",a.getTitle());
	}

	@Override
	public String request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("prop", "revisions");
		p.putData("format", "xml");
		p.putData("rvprop", "content");
		Document d = p.requestDocument();
		if(d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").size() == 0)
			return null;
		if(d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("revisions").size() == 0)
			return null;
		Element e = d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("revisions").get(0).getChildren("rev").get(0);
		return e.getText();
	}

}
