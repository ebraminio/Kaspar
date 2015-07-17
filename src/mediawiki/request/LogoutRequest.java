package mediawiki.request;

import java.io.IOException;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;


public class LogoutRequest extends MediaWikiRequest {

	@Override
	public Object request(MediaWikiConnection c) throws IOException  {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData("action", "logout");
		p.request();
		c.setLoginRequest(null);
		return null;
	}

}
