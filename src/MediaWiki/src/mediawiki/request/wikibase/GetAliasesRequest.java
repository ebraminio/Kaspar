package mediawiki.request.wikibase;


import java.util.ArrayList;

import javat.xml.Document;
import javat.xml.Element;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.wikibase.TranslatedContent;

public class GetAliasesRequest extends WikimediaRequest<TranslatedContent<ArrayList<String>>> {

	public GetAliasesRequest(String base, String...languages){
		setProperty("ids", base);
		String langs = "";
		for(String l : languages){
			langs += "|"+l;
		}
		langs = langs.substring(1);
		setProperty("languages", langs);
		
	}
	
	@Override
	public TranslatedContent<ArrayList<String>> request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetentities");
		p.putData("props", "aliases");
		Document d = p.requestDocument();
		TranslatedContent<ArrayList<String>> aliases = new TranslatedContent<>();
		for(Element e : d.getRootElement().getChildren("entities").get(0).getChildren("entity").get(0).getChildren("aliases").get(0).getChildren("alias")){
			ArrayList<String> vs;
			if(aliases.get(e.getAttribute("language").getValue()) == null){
				vs = new ArrayList<>();
			}else{
				vs = aliases.get(e.getAttribute("language").getValue());
			}
			vs.add(e.getAttribute("value").getValue());
			aliases.put(e.getAttribute("language").getValue(), vs);
		}
		return aliases;
	}

}
