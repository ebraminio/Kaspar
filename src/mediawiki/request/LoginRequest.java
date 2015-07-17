package mediawiki.request;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiException;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;

import javat.xml.Document;
import javat.xml.Element;

public class LoginRequest extends MediaWikiRequest<Object> {

	private String user;
	private String password;

	public LoginRequest(String user, String password){
		setUser(user);
		setPassword(password);
	}

	@Override
	public String request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData("action", "login");
		p.putData("lgname", getUser());
		p.putData("lgpassword", getPassword());
		
		Document d = p.requestDocument();
		Element answer = d.getRootElement().getChildren("login").get(0);
		
		String token = answer.getAttribute("token").getValue();
	//	c.setWikiname(answer.getAttribute("cookieprefix").getValue());
		
		MediaWikiPostRequest p2 = new MediaWikiPostRequest(c);
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
		throw new MediaWikiException("Login failed.");
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
