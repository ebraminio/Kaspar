package mediawiki.task;

import java.util.HashMap;
import java.util.List;

import datasets.in.GeoNames;

import main.GNDLoad;
import mediawiki.WikidataQuery;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaTask;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.WikibaseCoordinate;
import mediawiki.info.wikibase.WikibaseDate;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.request.wikibase.GetSpecificStatementRequest;

public class GeoNamesStateTask extends WikimediaTask {

	public GeoNamesStateTask(WikimediaConnection con) {
		super(con);
	}
	
	@Override
	public void run() {
		System.out.println("load country matrix");
		WikidataQuery countries = new WikidataQuery("CLAIM[297]");
		HashMap<String,String> c = new HashMap<>();
		
		try {
			List<Integer> c2 = countries.request();
			System.out.println(c2);
			for(Integer i : c2){
				c.put((String) getConnection().request(new GetSpecificStatementRequest("Q"+i, new Property(297))).get(0).getClaim().getSnak().getValue(), "Q"+i);
			}
			System.out.println(c.size()+" countries loaded");
			List<Integer> places = new WikidataQuery("CLAIM[625] AND NOCLAIM[17] AND CLAIM[131]").request();
			System.out.println(places.size()+" places loaded");
			for(Integer i : places){
				try{
					WikibaseCoordinate coord = (WikibaseCoordinate) getConnection().request(new GetSpecificStatementRequest("Q"+i, new Property(625))).get(0).getClaim().getSnak().getValue();
					if(coord == null)
						continue;
					String iso = GeoNames.getCountryCode(coord.getLatitude(), coord.getLongitude());
					String country = c.get(iso);
					if(country == null)
						continue;
					
					Reference ref = new Reference();
					ref.addClaim(new Claim(248, 830106));
					ref.addClaim(new Claim(813, new DateSnak(new WikibaseDate(WikibaseDate.ONE_DAY))));
					
					GNDLoad.addClaim(getConnection(), "Q"+i, new Claim(17, new ItemSnak(Integer.parseInt(country.substring(1)))), ref, "based on GeoNames");
				}catch(Exception e){
					System.err.println(e.getClass().getCanonicalName()+": "+e.getMessage());
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}
