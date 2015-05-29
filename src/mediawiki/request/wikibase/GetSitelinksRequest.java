package mediawiki.request.wikibase;

import java.util.ArrayList;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.Project;
import mediawiki.info.wikibase.Sitelink;

public class GetSitelinksRequest extends WikimediaRequest<ArrayList<Sitelink>> {

	public GetSitelinksRequest(String base){
		setProperty("ids", base);
	}
	
	@Override
	public ArrayList<Sitelink> request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetentities");
		p.putData("props", "sitelinks");
		Document d = p.requestDocument();
		Element e = d.getRootElement().getChildren("entities").get(0).getChildren("entity").get(0).getChildren("sitelinks").get(0);
		ArrayList<Sitelink> res = new ArrayList<>();
		for(Element link : e.getChildren("sitelink")){
			res.add(new Sitelink(new Project(link.getAttribute("site").getValue()), link.getAttribute("title").getValue()));
		}
		return res;
	}

}
