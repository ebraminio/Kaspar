package mediawiki.request;

import javat.xml.Document;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.Article;


public class WikiBaseItemRequest extends MediaWikiRequest<String> {

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
	public String request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("prop", "pageprops");
		Document d = p.requestDocument();
		if(d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("pageprops").size() == 0)
			return null;
		if(d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("pageprops").get(0).getAttribute("wikibase_item") == null)
			return null;
		return d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("pageprops").get(0).getAttribute("wikibase_item").getValue();
	}

}
