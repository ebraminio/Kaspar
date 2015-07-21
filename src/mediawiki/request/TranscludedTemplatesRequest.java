package mediawiki.request;

import java.util.ArrayList;
import java.util.List;

import javat.xml.Document;
import javat.xml.Element;
import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.Article;

public class TranscludedTemplatesRequest extends MediaWikiRequest<List<String>> {

	public TranscludedTemplatesRequest(String title) {
		setProperty("titles", title);
	}
	
	public TranscludedTemplatesRequest(String title, String template) {
		setProperty("titles", title);
		setProperty("tltemplates", template);
	}
	
	public TranscludedTemplatesRequest(Article title) {
		this(title.getTitle());
	}
	
	public TranscludedTemplatesRequest(Article title, String template) {
		this(title.getTitle(), template);
	}


	@Override
	public List<String> request(MediaWikiConnection c) throws Exception {
		ArrayList<String> r = new ArrayList<>();
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData("action", "query");
		p.putData("prop", "templates");
		p.putData(getProperties());
		p.putData("continue","");
		Document d = p.requestDocument();
		while(true){
			Element e = d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0);
			if(e.getChildren("templates").size() == 0)
				return r;
			else
				e = e.getChildren("templates").get(0);
			for(Element a : e.getChildren("tl")){
				r.add(a.getAttribute("title").getValue());
			}
			if(d.getRootElement().getChildren("continue").size() == 0)
				break;
			p.putData("tlcontinue", d.getRootElement().getChildren("continue").get(0).getAttribute("tlcontinue").getValue());
			d = p.requestDocument();
		}
		return r;
	}

}
