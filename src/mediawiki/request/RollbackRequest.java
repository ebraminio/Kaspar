package mediawiki.request;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;

public class RollbackRequest extends MediaWikiRequest<Object> implements ManipulativeRequest {

	public RollbackRequest(String title, String username){
		setProperty("title", title);
		setProperty("user", username);
	}
	
	@Override
	public Object request(MediaWikiConnection c) throws Exception {
		String token = c.request(new TokenRequest("rollback"));
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "rollback");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
