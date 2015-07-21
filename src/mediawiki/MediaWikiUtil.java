package mediawiki;

import java.util.ArrayList;

import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.SetReferenceRequest;

public class MediaWikiUtil {

	public static String formatLCCN(String lccn){
		String[] parts = lccn.split("\\/");
		if(parts.length != 3)
			return null; // throw new IllegalArgumentException("Malformed LCCN identifier: "+lccn);
		String flccn = parts[0]+parts[1];
		for(int i = 0; i < 6-parts[2].length(); i++){
			flccn += "0";
		}
		flccn += parts[2];
		return flccn;
	}
	
	public static String[] splitLCCN(String lccn) {
		if(lccn.matches("^(|n|nb|nr|no|ns|sh|sj|sn)(\\d+)(\\d{6})$"))
	        return lccn.replaceAll("^(|n|nb|nr|no|ns|sh|sj|sn)(\\d+)(\\d{6})$", "$1/$2/$3").split("\\/");
	    if(lccn.matches("^(|n|nb|nr|no|ns|sh|sj|sn)\\/\\d{2,4}\\/\\d+$"))
	    	return lccn.split("\\/");
	    return null;
	}
	
	public static Statement addTrustedStatement(MediaWikiConnection wikidata, String base, Claim c, Reference ref) throws Exception {
		ArrayList<Statement> s = wikidata.request(new GetSpecificStatementRequest(base, c.getProperty()));
		if(s.size() == 0) {
			Statement s1 = wikidata.request(new CreateClaimRequest(base, c));
			wikidata.request(new SetReferenceRequest(s1, ref));
			return s1;
		}else{
			for(Statement s1 : s) {
				if(s1.getClaim().equals(c)){
					if(! s1.hasReference(new Property(248) ))
						wikidata.request(new SetReferenceRequest(s1, ref));
					return null; // sonst würden eventuell Qualifiers erstellt werden
				}
			}
			
			/**
			 * mehrere zu der Property wurden gefunden, keiner mit diesem Wert
			 */
			return null;
		}
	}
	
	private static final String persianDigits = "۰۱۲۳۴۵۶۷۸۹";
	private static final String arabicDigits 	= "0123456789";
	
	public static String parsePersianNumber(String persian) {
		for(int i = 0; i < persianDigits.length() && i < arabicDigits.length(); i++) 
			persian = persian.replaceAll(""+persianDigits.charAt(i), ""+arabicDigits.charAt(i));
		return persian;
	}
	
	public static boolean containsPersianDigits(String s) {
		for(char c : persianDigits.toCharArray())
			if(s.indexOf(c) >= 0)
				return true;
		return false;
	}

}
