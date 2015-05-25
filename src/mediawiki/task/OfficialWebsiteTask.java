package mediawiki.task;

import java.net.URL;
import java.util.List;
import java.util.Map;

import mediawiki.WikimediaConnection;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.URLSnak;
import mediawiki.request.GetTemplatesValuesRequest;
import mediawiki.request.TemplateEmbeddedInRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.SetReferenceRequest;

public class OfficialWebsiteTask extends WikipediaWikidataTask {

	public OfficialWebsiteTask(WikimediaConnection wikidata,
			WikimediaConnection wikipedia) {
		super(wikidata, wikipedia);
	}

	@Override
	public void run() {
		try {
			List<Article> l = getWikipediaConnection().request(new TemplateEmbeddedInRequest("Template:Official website", 0));
			for(Article a : l){
				try{
					String base = getWikipediaConnection().request(new WikiBaseItemRequest(a));
					Map<String, String> m = getWikipediaConnection().request(new GetTemplatesValuesRequest(a, "Official website")).get(0);
					String url = m.get("1");
					if(url.indexOf("//") == -1)
						url = "http://"+url;
					URL u = new URL(url);
					List<Statement> c = getWikidataConnection().request(new GetSpecificStatementRequest(base, new Property(856)));
					if(c.size() == 0){
						Statement s = getWikidataConnection().request(new CreateClaimRequest(base, new Claim(856, new URLSnak(u))));
						getWikidataConnection().request(new SetReferenceRequest(s, new Reference(new Property(143), new ItemSnak(328))));
					}
				}catch(Exception e2){
					e2.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
