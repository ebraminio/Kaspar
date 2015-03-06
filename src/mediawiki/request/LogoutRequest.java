package mediawiki.request;

import java.io.IOException;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;


public class LogoutRequest extends WikimediaRequest {

	@Override
	public Object request(WikimediaConnection c) throws IOException  {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData("action", "logout");
		p.request();
		c.setLoginRequest(null);
		return null;
	}

}
