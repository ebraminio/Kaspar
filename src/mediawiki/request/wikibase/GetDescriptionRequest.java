package mediawiki.request.wikibase;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;


public class GetDescriptionRequest extends MediaWikiRequest<String> {

	private String language; 
	
	public GetDescriptionRequest(String entity, String language){
		this.language = language;
		setProperty("ids",(! entity.startsWith("Q") ? "Q" : "")+entity);
	}
	
	@Override
	public String request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetentities");
		p.putData("props", "descriptions");
		setProperty("languages", language);
		Document d = p.requestDocument();
		Element e = d.getRootElement().getChildren("entities").get(0).getChildren("entity").get(0).getChildren("descriptions").get(0);
		for(Element e2 : e.getChildren("description")){
			if(e2.getAttribute("language").getValue().equals(language)){
				return e2.getAttribute("value").getValue();
			}
		}
		return null;
	}


}
