package mediawiki.request;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;

public class RollbackRequest extends WikimediaRequest<Object> implements ManipulativeRequest {

	public RollbackRequest(String title, String username){
		setProperty("title", title);
		setProperty("user", username);
	}
	
	@Override
	public Object request(WikimediaConnection c) throws Exception {
		String token = c.request(new TokenRequest("rollback"));
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "rollback");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
