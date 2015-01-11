package main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.security.auth.callback.LanguageCallback;
import javax.xml.stream.XMLStreamException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xml.sax.SAXException;

import wikimedia.*;
import wikimedia.info.Article;
import wikimedia.info.wikibase.Claim;
import wikimedia.info.wikibase.EntityValue;
import wikimedia.info.wikibase.Value;
import wikimedia.request.*;
import wikimedia.request.wikibase.AddClaimRequest;
import wikimedia.request.wikibase.HasClaimRequest;
import wikimedia.request.wikibase.SetReferenceRequest;

import javat.xml.Document;
import javat.xml.Element;

public class KasparMain {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws XMLStreamException 
	 * @throws JSONException 
	 */
	public static void main(String[] args) throws IOException, XMLStreamException, SAXException, JSONException {
		WikimediaConnection wikidata = new WikimediaConnection("www","wikidata.org");
		wikidata.request(new LoginRequest("USERNAME", "PASSWORT"));
				
		WikimediaConnection wikipedia = new WikimediaConnection("de","wikipedia.org");
		wikipedia.request(new LoginRequest("USERNAME", "PASSWORT"));
		
		
		changeWikidataInCategory(wikidata,wikipedia,"Kategorie:Jordanier","P27",810,"P143",48183);
		
	}
	
	/**
	 * Add claims to existing Wikidata items based on defined Wikipedia category classification
	 * @param wikidata WikimediaConnection to the Wikidata API
	 * @param wikipedia WikimediaConnection to the Wikipedia API (e.g. de.wikipedia)
	 * @param kat the Wikipedia category which should be scanned (e.g. Kategorie:Jordanier)
	 * @param prop the Wikidata property which should be added (e.g. P27)
	 * @param propvalue a Wikidata item (without Q at the beginning) which should be the value (e.g. 810)
	 * @param sourceprop the Wikidata property which should be added as references (e.g. P143)
	 * @param sourceentity a Wikidata item (without Q at the beginning) which should be the value of the reference (e.g. 48183)
	 * @throws IOException
	 * @throws XMLStreamException
	 * @throws SAXException
	 * @throws JSONException
	 */
	public static void changeWikidataInCategory(
			WikimediaConnection wikidata,
			WikimediaConnection wikipedia,
			String kat,
			String prop,
			int propvalue,
			String sourceprop,
			int sourceentity
			) throws IOException, XMLStreamException, SAXException, JSONException{
		System.out.println("Looking up [["+wikipedia.getLanguage()+":"+kat+"]]...");
		List<Article> r = (List<Article>) wikipedia.request(new CategoryMemberRequest(kat,0));
		System.out.println(r.size()+" articles in [["+wikipedia.getLanguage()+":"+kat+"]]");
		int i = 0;
		
		int ohne = 0;
		int bereits = 0;
		int geändert = 0;
		
		for(Article a : r){
		//	if(a.getPageid() == 7724000 || a.getPageid() == 1096668 || a.getPageid() == 925068)
		//		continue;
			i++;
			System.out.print(i+".\t");
			String base;
			try{
				base = (String) wikipedia.request(new WikiBaseItemRequest(a));
			}catch(NullPointerException e){
				System.out.println(a.getTitle()+" without Wikidata item");
				ohne++;
				continue;
			}
			Boolean result = (Boolean) wikidata.request(new HasClaimRequest(base,prop));
			if(result){
				System.out.println(a.getTitle()+" ("+base+") has already a claim for "+prop);
				bereits++;
				continue;
			}
			JSONObject value = new JSONObject();
			value.put("entity-type", "item");
			value.put("numeric-id", propvalue);
			AddClaimRequest req = new AddClaimRequest(base, prop, value);
			req.setProperty("summary", "processed by [[User:KasparBot/Kaspar|Kaspar 1.0]] based on [["+wikipedia.getLanguage()+":"+kat+"]]");
			Claim claim = (Claim) wikidata.request(req);
			WikimediaRequest req2 = new SetReferenceRequest(claim.getId(), sourceprop, new EntityValue(sourceentity));
			req2.setProperty("summary", "processed by [[User:KasparBot/Kaspar|Kaspar 1.0]] based on [["+wikipedia.getLanguage()+":"+kat+"]]");
			wikidata.request(req2);
			System.out.println(a.getTitle()+" ("+base+") edited.");
			geändert++;
		} 
		System.out.println("Completed:");
		System.out.println(ohne+"\t without Wikidata item");
		System.out.println(bereits+"\t have already a claim for "+prop);
		System.out.println(geändert+"\t edited");
	}

}
