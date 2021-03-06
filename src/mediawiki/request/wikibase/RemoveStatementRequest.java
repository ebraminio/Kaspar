package mediawiki.request.wikibase;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class RemoveStatementRequest extends MediaWikiRequest<Object> implements ManipulativeRequest {

	public RemoveStatementRequest(Statement s) {
		setProperty("claim", s.getId());
	}

	@Override
	public Object request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbremoveclaims");
		p.putData("token", c.request(new TokenRequest()));
		p.requestDocument();
		return null;
	}

}
