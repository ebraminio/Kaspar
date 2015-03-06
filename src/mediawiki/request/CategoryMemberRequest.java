package mediawiki.request;

import java.util.ArrayList;
import java.util.List;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.Article;


public class CategoryMemberRequest extends WikimediaRequest {

	private int limit = Integer.MAX_VALUE; 
	
	public CategoryMemberRequest(String kat) {
		setProperty("cmtitle", kat);
		setProperty("cmlimit", 5000);
	}
	
	public CategoryMemberRequest(String kat, int namespace) {
		this(kat);
		setProperty("cmnamespace", namespace);
	}
	
	public CategoryMemberRequest(String kat, int namespace, int limit){
		this(kat,namespace);
		this.limit = limit;
	}

	@Override
	public List<Article> request(WikimediaConnection c) throws Exception {
		ArrayList<Article> r = new ArrayList<>();
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("list", "categorymembers");
		Document d = p.requestDocument();
		while(true){
			Element e = d.getRootElement().getChildren("query").get(0).getChildren("categorymembers").get(0);
			for(Element a : e.getChildren("cm")){
				r.add(Article.convert(a));
				if(r.size() >= limit)
					return r;
			}
			if(d.getRootElement().getChildren("continue").size() == 0)
				break;
			p.putData("cmcontinue", d.getRootElement().getChildren("continue").get(0).getAttribute("cmcontinue").getValue());
			d = p.requestDocument();
		}
		
		return r;
	}

}
