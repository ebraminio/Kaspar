package mediawiki.request.wikibase;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.wikibase.Sitelink;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class SetSitelinkRequest extends MediaWikiRequest<Object> implements
		ManipulativeRequest {
	
	public SetSitelinkRequest(String base, Sitelink l) {
		setProperty("id", base);
		setProperty("linksite", l.getProject().getSite());
		setProperty("linktitle", l.getTitle());
	}

	@Override
	public Object request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetsitelink");
		p.putData("token", c.request(new TokenRequest()));
		p.requestDocument();
		return null;
	}

}
