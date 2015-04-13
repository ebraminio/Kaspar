package main;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaUtil;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Sitelink;
import mediawiki.request.ContentRequest;
import mediawiki.request.GetTemplatesValuesRequest;
import mediawiki.request.LoginRequest;
import mediawiki.request.TemplateEmbeddedInRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.GetSitelinksRequest;
import mediawiki.task.CommonscatTask;
import mediawiki.task.CoordinatesTask;

public class Test3 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	
	public static void main(String[] args) throws Exception {
		String username = Config.USERNAME;
		String password = Config.PASSWORD;
		
		WikimediaConnection wikipedia = new WikimediaConnection("en","wikipedia.org");
		wikipedia.request(new LoginRequest(username, password));
		
		TemplateEmbeddedInRequest p = new TemplateEmbeddedInRequest("Template:Authority control",0);
		p.setLimit(1000);
		List<Article> a = wikipedia.request(p);
		for(Article art : a){
			String base = wikipedia.request(new WikiBaseItemRequest(art));
			Map<String, String> m = wikipedia.request(new GetTemplatesValuesRequest(art, "Authority control")).get(0);
			if(m.size() == 0)
				continue;
			if(m.get("LCCN") != null){
				String lccn = m.get("LCCN");
				System.out.println(lccn);
				
				System.out.println(WikimediaUtil.formatLCCN(lccn));
			}
		}
		
		String content = wikipedia.request(new ContentRequest("Barack_Obama"));
		
		content = content.replaceAll("\\{\\{Authority\\ control[\\|A-Za-z0-9\\=\\ \\/\\-]+\\}\\}", "{{Authority control}}");
		System.out.println(content);
		
	}

}
