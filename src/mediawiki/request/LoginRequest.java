package mediawiki.request;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaException;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;

import javat.xml.Document;
import javat.xml.Element;

public class LoginRequest extends WikimediaRequest {

	private String user;
	private String password;

	public LoginRequest(String user, String password){
		setUser(user);
		setPassword(password);
	}

	@Override
	public String request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData("action", "login");
		p.putData("lgname", getUser());
		p.putData("lgpassword", getPassword());
		
		Document d = p.requestDocument();
		Element answer = d.getRootElement().getChildren("login").get(0);
		
		String token = answer.getAttribute("token").getValue();
		c.setWikiname(answer.getAttribute("cookieprefix").getValue());
		
		WikimediaPostRequest p2 = new WikimediaPostRequest(c);
		p2.putData("action", "login");
		p2.putData("lgname", getUser());
		p2.putData("lgpassword", getPassword());
		p2.putData("lgtoken", token);
		
		d = p2.requestDocument();
		answer = d.getRootElement().getChildren("login").get(0);
		
		if(answer.getAttribute("result").getValue().equals("Success")){
			c.setLoggedIn(true);
			return answer.getAttribute("lguserid").getValue();
		}
		throw new WikimediaException("Login failed.");
	}
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
