package mediawiki.request.wikibase;

import java.util.ArrayList;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.Project;
import mediawiki.info.wikibase.Sitelink;

public class GetSitelinksRequest extends MediaWikiRequest<ArrayList<Sitelink>> {

	public GetSitelinksRequest(String base){
		setProperty("ids", base);
	}
	
	@Override
	public ArrayList<Sitelink> request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
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
