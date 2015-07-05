package mediawiki.request;

import javat.xml.Document;
import javat.xml.Element;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Sitelink;

public class ArticleForNameRequest extends WikimediaRequest<Article> {

	public ArticleForNameRequest(String title){
		setProperty("titles", title);
	}
	
	public ArticleForNameRequest(Sitelink sl){
		this(sl.getTitle());
	}
	
	@Override
	public Article request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("prop", "info");
		Document d = p.requestDocument();
		Element e = d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0);
		if(e.getAttribute("missing") != null)
			return null;
		return new Article(Integer.parseInt(e.getAttribute("pageid").getValue()), Integer.parseInt(e.getAttribute("ns").getValue()), e.getAttribute("title").getValue());
	}

}
