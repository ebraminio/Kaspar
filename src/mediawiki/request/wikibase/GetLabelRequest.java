package mediawiki.request.wikibase;

import javat.xml.Document;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;


public class GetLabelRequest extends WikimediaRequest<String> {

	public GetLabelRequest(String language, String base) {
		super();
		setProperty("ids", base);
		setProperty("languages", language);
	}
	
	@Override
	public String request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetentities");
		p.putData("props", "labels");
		Document d = p.requestDocument();
		if(d.getRootElement().getChildren("entities").get(0).getChildren("entity").get(0).getChildren("labels").get(0).getChildren("label").size() == 0)
			return null;
		return d.getRootElement().getChildren("entities").get(0).getChildren("entity").get(0).getChildren("labels").get(0).getChildren("label").get(0).getAttribute("value").getValue();
	}

}
