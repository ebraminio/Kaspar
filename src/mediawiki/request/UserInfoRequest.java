package mediawiki.request;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;

public class UserInfoRequest extends WikimediaRequest<String> {

	public UserInfoRequest() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData("action", "query");
		p.putData("meta", "userinfo");
		return p.request();
	}

}
