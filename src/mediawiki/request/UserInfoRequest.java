package mediawiki.request;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;

public class UserInfoRequest extends MediaWikiRequest<String> {

	public UserInfoRequest() {
	}

	@Override
	public String request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData("action", "query");
		p.putData("meta", "userinfo");
		return p.request();
	}

}
