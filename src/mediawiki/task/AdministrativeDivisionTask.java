package mediawiki.task;


import java.util.List;

import main.GNDLoad;
import mediawiki.WikidataQuery;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaTask;
import mediawiki.info.wikibase.Claim;

public class AdministrativeDivisionTask extends WikimediaTask {

	public AdministrativeDivisionTask(WikimediaConnection con) {
		super(con);
	}

	@Override
	public void run() {
		System.out.println("load country matrix");
		WikidataQuery countries = new WikidataQuery("CLAIM[297]");
		try {
			List<Integer> c = countries.request();
			
			for(Integer i : c){
				WikidataQuery places = new WikidataQuery("CLAIM[131:(TREE["+i+"][][131])] AND NOCLAIM[17]");
				for(Integer p : places.request()){
					GNDLoad.addClaim(getConnection(), "Q"+p, new Claim(17,i), null, "based on [[Property:P131]]");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
