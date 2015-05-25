package mediawiki.request.wikibase;

import java.util.ArrayList;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaRequest;
import mediawiki.info.wikibase.Sitelink;

public class GetSitelinkRequest extends WikimediaRequest<Sitelink> {
	
	private GetSitelinksRequest r;

	public GetSitelinkRequest(String base) {
		r = new GetSitelinksRequest(base);
	}
	
	public GetSitelinkRequest(String base, String site) {
		r = new GetSitelinksRequest(base);
		r.setProperty("sitefilter", site);
	}
	
	@Override
	public Sitelink request(
			WikimediaConnection c) throws Exception {
		ArrayList<Sitelink> a = r.request(c);
		return (a.size() == 0 ? null : a.get(0));
	}

}
