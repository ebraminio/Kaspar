package mediawiki.request;


import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;

public class UserDailyContributionsRequest extends MediaWikiRequest<Integer> {

	public UserDailyContributionsRequest(String user){
		setProperty("user", user);
	}
	
	@Override
	public Integer request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "userdailycontribs");
		return Integer.parseInt(p.requestDocument().getRootElement().getChildren("userdailycontribs").get(0).getAttribute("totalEdits").getValue());
	}

}
