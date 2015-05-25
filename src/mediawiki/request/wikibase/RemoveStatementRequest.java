package mediawiki.request.wikibase;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.wikibase.Statement;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class RemoveStatementRequest extends WikimediaRequest<Object> implements ManipulativeRequest {

	public RemoveStatementRequest(Statement s) {
		setProperty("claim", s.getId());
	}

	@Override
	public Object request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbremoveclaims");
		p.putData("token", c.request(new TokenRequest()));
		p.requestDocument();
		return null;
	}

}
