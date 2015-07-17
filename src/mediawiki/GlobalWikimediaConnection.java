package mediawiki;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mediawiki.info.Project;

public class GlobalWikimediaConnection {

	private HashMap<String, String> centralcookies = new HashMap<>();
	
	private boolean login = false;
	private boolean bot = false;
	private boolean teststate = false;
	
	public GlobalWikimediaConnection() {
	}

	public <T> T request(Project p, MediaWikiRequest<T> r) throws Exception{
		return openConnection(p).request(r);
	}
	
	private MediaWikiConnection applySetting(MediaWikiConnection connection){
		connection.putCookies(centralcookies);
		if(login)
			connection.setLoggedIn(login);
		if(bot)
			connection.setBot(bot);
		if(teststate)
			connection.setTestState(teststate);
		connection.setGlobalConnection(this);
		return connection;
	}
	
	public MediaWikiConnection openConnection(Project p){
		MediaWikiConnection connection = new MediaWikiConnection(p);
		connection = applySetting(connection);
		return connection;
	}
	
	public MediaWikiConnection openConnection(String l, String p){
		MediaWikiConnection connection = new MediaWikiConnection(l,p);
		connection = applySetting(connection);
		return connection;
	}
	
	public MediaWikiConnection openConnection(String api){
		MediaWikiConnection connection = new MediaWikiConnection(api);
		connection = applySetting(connection);
		return connection;
	}
	
	public void putGlobalCookie(String k, String v){
		if(k.matches("centralauth\\_.*"))
			centralcookies.put(k, v);
	}
	
	public void putGlobalCookies(Map<String,String> m){
		for(Entry<String,String> e : m.entrySet()){
			if(! e.getKey().matches("centralauth\\_.*"))
				m.remove(e.getKey());
		}
		centralcookies.putAll(m);
	}
	
	protected void setLoggedIn(boolean l){
		login = l;
	}

	public boolean isBot() {
		return bot;
	}

	public boolean isTestState() {
		return teststate;
	}

	public void setBot(boolean bot) {
		this.bot = bot;
	}

	public void setTestState(boolean teststate) {
		this.teststate = teststate;
	}
	
}
