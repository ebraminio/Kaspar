package mediawiki.request.wikibase;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;


public class GetDescriptionRequest extends WikimediaRequest<String> {

	public GetDescriptionRequest(String entity, String language){
		setProperty("languages", language);
		setProperty("ids",(! entity.startsWith("Q") ? "Q" : "")+entity);
	}
	
	@Override
	public String request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetentities");
		p.putData("props", "descriptions");
		Document d = p.requestDocument();
		Element e = d.getRootElement().getChildren("entities").get(0).getChildren("entity").get(0).getChildren("descriptions").get(0);
		for(Element e2 : e.getChildren("description")){
			if(e2.getAttribute("language").getValue().equals(getProperty("languages"))){
				return e2.getAttribute("value").getValue();
			}
		}
		return null;
	}

}
