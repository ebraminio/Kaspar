package mediawiki.request;


import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;

public class UserDailyContributionsRequest extends WikimediaRequest<Integer> {

	public UserDailyContributionsRequest(String user){
		setProperty("user", user);
	}
	
	@Override
	public Integer request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "userdailycontribs");
		return Integer.parseInt(p.requestDocument().getRootElement().getChildren("userdailycontribs").get(0).getAttribute("totalEdits").getValue());
	}

}
