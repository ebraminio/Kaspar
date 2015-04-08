package mediawiki.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaRequest;
import mediawiki.info.Article;

public class GetTemplatesValuesRequest extends WikimediaRequest<List<Map<String, String>>> {

	private String title;
	private String template;
	
	
	public GetTemplatesValuesRequest(Article article, String template){
		this(article.getTitle(), template);
	}
	
	public GetTemplatesValuesRequest(String title, String template){
		this.title = title;
		this.template = template;
	}
	
	@Override
	public List<Map<String, String>> request(WikimediaConnection c)
			throws Exception {
		List<Map<String, String>> res = new ArrayList<>();
		
		String text = (String) c.request(new ContentRequest(title));
		Document d = (Document) c.request(new ParseTemplatesRequest(text,title));
		
		for(Element e : d.getRootElement().getChildren("template")){
			if(e.getChildren("title").get(0).getText().trim().equalsIgnoreCase(template)){
				HashMap<String, String> r = new HashMap<>();
				int i = 1;
				for(Element e2 : e.getChildren("part")){
					String key = e2.getChildren("name").get(0).getText().trim();
					if(key.equals("")){
						key = i+"";
						i++;
					}
 					r.put(key, e2.getChildren("value").get(0).getText().trim());
				}
				res.add(r);
			}
		}
		
		return res;
	}

}
