package mediawiki.request;

import java.util.HashMap;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaRequest;
import mediawiki.info.Article;


public class GetTemplateValuesRequest extends WikimediaRequest<HashMap<String, String>> {

	private String title;
	private String template;
	
	
	public GetTemplateValuesRequest(Article article, String template){
		this(article.getTitle(), template);
	}
	
	public GetTemplateValuesRequest(String title, String template){
		this.title = title;
		this.template = template;
	}
	
	@Override
	public HashMap<String, String> request(WikimediaConnection c) throws Exception {
		
		HashMap<String, String> r = null;
		
		String text = (String) c.request(new ContentRequest(title));
		Document d = (Document) c.request(new ParseTemplatesRequest(text,title));
		
		
		for(Element e : d.getRootElement().getChildren("template")){
			if(e.getChildren("title").get(0).getText().trim().equalsIgnoreCase(template)){
				r = new HashMap<>();
				int i = 1;
				for(Element e2 : e.getChildren("part")){
					String key = e2.getChildren("name").get(0).getText().trim();
					if(key.equals("")){
						key = i+"";
						i++;
					}
 					r.put(key, e2.getChildren("value").get(0).getText().trim());
				}
			}
		}
		
		return r;
	}

}
