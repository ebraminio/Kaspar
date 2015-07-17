package mediawiki.request;

import java.util.Map;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiRequest;
import mediawiki.info.Article;

@Deprecated
public class GetTemplateValuesRequest extends MediaWikiRequest<Map<String, String>> {

	private String title;
	private String template;
	
	@Deprecated
	public GetTemplateValuesRequest(Article article, String template){
		this(article.getTitle(), template);
	}
	
	@Deprecated
	public GetTemplateValuesRequest(String title, String template){
		this.title = title;
		this.template = template;
	}
	
	@Override @Deprecated
	public Map<String, String> request(MediaWikiConnection c) throws Exception {
		
		return c.request(new GetTemplatesValuesRequest(title, template)).get(0);
		
	}

}
