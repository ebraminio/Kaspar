package main;

import static main.GNDLoad.addClaim;
import static main.GNDLoad.importDate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javat.xml.Element;
import mediawiki.WikidataQuery;
import mediawiki.WikimediaConnection;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.WikibaseDate;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.LoginRequest;
import mediawiki.request.wikibase.GetLabelRequest;
import mediawiki.request.wikibase.HasClaimRequest;
import mediawiki.request.wikibase.SetLabelRequest;
import datasets.in.GND;

public class GNDSetInformation {

	public static void importPlace(WikimediaConnection wikidata, String base, Element e, String tag, int prop, Reference ref) throws Exception{
		importPlace(wikidata, base, e, tag, prop, ref, null, "");
	}
	public static void importPlace(WikimediaConnection wikidata, String base, Element e, String tag, int prop, Reference ref, String referer, String condition) throws Exception{
		if(e.getChildren(tag).size() > 0 && e.getChildren(tag).get(0).getChildren("Description").size() > 0){
			if((Boolean)wikidata.request(new HasClaimRequest(base, new Property(prop))) == false){
				for(Element e2 : e.getChildren(tag)){
					String gnd = e2.getChildren("Description").get(0).getAttribute("about").getValue().substring("http://d-nb.info/gnd/".length());
					WikidataQuery q = new WikidataQuery("STRING[227:\""+gnd+"\"] AND ("+condition+")");
					List<Integer> wqresult = q.request(); 
					if(wqresult.size() == 1){
						int place = wqresult.get(0);
						KasparMain.addWikidataProperty(wikidata,base, new Claim(prop,place), ref, "processed by KasparBot based on GND");
						System.out.println("P"+prop+" created: "+place);
					}
				}
			}
		}
	}
	

}
