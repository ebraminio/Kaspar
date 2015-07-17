package mediawiki.task;

import java.util.List;
import java.util.Map;

import main.GNDLoad;
import mediawiki.MediaWikiConnection;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.WikibaseCoordinate;
import mediawiki.info.wikibase.snaks.CoordinateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.request.GetTemplatesValuesRequest;
import mediawiki.request.TemplateEmbeddedInRequest;
import mediawiki.request.WikiBaseItemRequest;

public class CoordinatesTask extends WikipediaWikidataTask {

	public CoordinatesTask(MediaWikiConnection wikidata,
			MediaWikiConnection wikipedia) {
		super(wikidata, wikipedia);
	}

	@Override
	public void run() {
		try{
			TemplateEmbeddedInRequest p = new TemplateEmbeddedInRequest("Vorlage:Coordinate", 0);
			p.setProperty("eidir", "descending");
			List<Article> a = getWikipediaConnection().request(p);
			setTogo(a.size());
			System.out.println("Alle Koordinaten geladen!");
			for(Article ac : a){
				if(isStopped())
					return;
				try{
					if(ac.getTitle().indexOf("Liste") >= 0)
						continue;
					String base = (String) getWikipediaConnection().request(new WikiBaseItemRequest(ac));
					if(base == null){
						increaseDone();
						continue;
					}
					if(getWikipediaConnection().request(new GetTemplatesValuesRequest(ac, "All coordinates")).size() > 0){
						increaseDone();
						continue;
					}
					List<Map<String,String>> ts = getWikipediaConnection().request(new GetTemplatesValuesRequest(ac, "Coordinate"));
					if(ts.size() != 1){
						increaseDone();
						continue;
					}
					Map<String, String> v = ts.get(0);
					
					if(v.containsKey("globe") && !v.get("globe").equals("earth") && !v.get("globe").equals("")){
						increaseDone();
						continue;
					}
					
					if(v.get("NS").equals("") || v.get("EW").equals("")){
						increaseDone();
						continue;
					}
					
					WikibaseCoordinate coord = WikibaseCoordinate.parse(v.get("NS"), v.get("EW"));
					
					GNDLoad.addClaim(getConnection(), base, new Claim(625, new CoordinateSnak(coord)), new Reference(new Property(143), new ItemSnak(48183)),"based on coordinates-template on dewiki");
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
