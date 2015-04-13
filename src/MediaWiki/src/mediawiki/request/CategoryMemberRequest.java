package mediawiki.request;

import java.util.HashMap;
import java.util.Map;

import javat.xml.Element;

import mediawiki.ContinuingRequest;
import mediawiki.info.Article;


public class CategoryMemberRequest extends ContinuingRequest<Article> {

	public CategoryMemberRequest(String kat) {
		super("categorymembers", "cm", "cm");
		setProperty("cmtitle", kat);
		setProperty("cmlimit", 5000);
	}
	
	public CategoryMemberRequest(String kat, int namespace) {
		this(kat);
		setProperty("cmnamespace", namespace);
	}
	
	public CategoryMemberRequest(String kat, int namespace, int limit){
		this(kat,namespace);
		setLimit(limit);
	}

	@Override
	protected Article parse(Element e) {
		return Article.convert(e);
	}
	
	@Override
	protected Map<String, String> getRequiredParameters() {
		HashMap<String, String> p = new HashMap<>();
		p.put("action", "query");
		p.put("list", "categorymembers");;
		return p;
	}

}
