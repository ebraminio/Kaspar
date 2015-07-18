package mediawiki.request;

import java.util.HashMap;
import java.util.Map;

import util.Util;

import javat.xml.Element;

import mediawiki.ContinuingRequest;
import mediawiki.info.Article;


public class CategoryMemberRequest extends ContinuingRequest<Article> {

	public CategoryMemberRequest(String kat) {
		super("categorymembers", "cm", "cm");
		setProperty("cmtitle", kat);
		setProperty("cmlimit", 5000);
	}
	
	public CategoryMemberRequest(String kat, Integer...namespace) {
		this(kat);
		setProperty("cmnamespace", Util.implode(namespace, "|"));
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
