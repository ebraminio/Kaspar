package mediawiki.request;

import java.util.HashMap;
import java.util.Map;

import javat.xml.Element;
import mediawiki.ContinuingRequest;
import mediawiki.info.Article;

public class TemplateEmbeddedInRequest extends ContinuingRequest<Article> {

	
	public TemplateEmbeddedInRequest(String template, int namespace){
		this(template);
		setProperty("einamespace", namespace);
	}
	
	public TemplateEmbeddedInRequest(String template) {
		super("embeddedin", "ei", "ei");
		setProperty("eititle", template);
		setProperty("eilimit", 5000+"");
	}

	@Override
	protected Article parse(Element e) {
		return Article.convert(e);
	}

	@Override
	protected Map<? extends String, ? extends String> getRequiredParameters() {
		HashMap<String, String> p = new HashMap<>();
		p.put("action", "query");
		p.put("list", "embeddedin");
		return p;
	}
	
	
}
