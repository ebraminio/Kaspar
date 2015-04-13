package mediawiki.task;

import java.util.HashMap;
import java.util.List;

import main.GNDLoad;
import mediawiki.WikimediaConnection;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.GetTemplateValuesRequest;
import mediawiki.request.TemplateEmbeddedInRequest;
import mediawiki.request.WikiBaseItemRequest;

public class CommonscatTask extends WikipediaWikidataTask {

	public CommonscatTask(WikimediaConnection wikidata,
			WikimediaConnection wikipedia) {
		super(wikidata, wikipedia);
	}

	@Override
	public void run() {
		try{
			TemplateEmbeddedInRequest p = new TemplateEmbeddedInRequest("Vorlage:Commonscat", 0);
			p.setProperty("eidir", "descending");
			List<Article> a = getWikipediaConnection().request(p);
			setTogo(a.size());
			System.out.println("Alle Commons geladen!");
			for(Article ac : a){
				if(isStopped())
					return;
				try{
					String base = (String) getWikipediaConnection().request(new WikiBaseItemRequest(ac));
					if(base == null){
						increaseDone();
						continue;
					}
					
					HashMap<String, String> v = getWikipediaConnection().request(new GetTemplateValuesRequest(ac, "Commonscat"));
					String commonscat = v.get("1");
					commonscat = commonscat == null || commonscat.trim().equals("") ? ac.getTitle() : commonscat;
					
					GNDLoad.addClaim(getConnection(), base, new Claim(373, new StringSnak(commonscat)), new Reference(new Property(143), new ItemSnak(48183)), "based on commonscat-Template on dewiki");
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}
				increaseDone();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		fireCompletedEvent();
	}

}
