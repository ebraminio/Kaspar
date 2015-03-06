package mediawiki;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mediawiki.request.LoginRequest;
import mediawiki.request.LogoutRequest;
import mediawiki.request.ManipulativeRequest;


public class WikimediaConnection {

	private String wikiname = "";
	private String apihref = "";
	private String language = "";
	
	private LoginRequest loginrequest = null;
	private boolean login = false;
	private boolean bot = false;
	
	private HashMap<String, String> cookies = new HashMap<>();
	
	private Map<Class<? extends WikimediaRequest>, Integer> statistic = new HashMap<>();
	
	private final Object editsynchronizer = new Object();
	
	public WikimediaConnection(String apihref){
		setApihref(apihref);
	}
	
	public WikimediaConnection(String language, String wikiname){
		this("https://"+language+"."+wikiname+"/w/api.php");
		setWikiname(wikiname);
		setLanguage(language);
	}

	public String getWikiname() {
		return wikiname;
	}

	public String getApihref() {
		return apihref;
	}

	public void setWikiname(String wikiname) {
		this.wikiname = wikiname;
	}

	public void setApihref(String apihref) {
		this.apihref = apihref;
	}

	public Object request(WikimediaRequest r) throws Exception{
		if(r instanceof LoginRequest)
			setLoginRequest((LoginRequest)r);
		log(r);
		Object o = null;
		if(r instanceof ManipulativeRequest){
			synchronized(editsynchronizer){
				o = r.request(this);
			}
		}else{
			o = r.request(this);
		}
		return o;
	}
	
	private void log(WikimediaRequest r){
		if(statistic.containsKey(r.getClass())){
			statistic.put(r.getClass(), statistic.get(r.getClass())+1);
		}else{
			statistic.put(r.getClass(), 1);
		}
	}
	
	public void putCookie(String k, String v) {
		cookies.put(k, v);
	}
	
	public void putCookies(Map<String, String> m){
		cookies.putAll(m);
	}
	
	public Map<String, String> getCookies(){
		return cookies;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isBot() {
		return bot;
	}

	public void setBot(boolean bot) {
		this.bot = bot;
	}
	
	public void close() throws IOException {
		new LogoutRequest().request(this);
		loginrequest = null;
		login = false;
		cookies.clear();
		statistic.clear();
	}

	
	protected LoginRequest getLoginRequest() {
		return loginrequest;
	}
	
	public boolean isLoggedIn(){
		return login;
	}
	
	public void setLoggedIn(boolean l){
		login = l;
	}

	public void setLoginRequest(LoginRequest loginrequest) {
		this.loginrequest = loginrequest;
	}
	
	public Object relogin() throws Exception {
		return request(getLoginRequest());
	}
	
	public Map<Class<? extends WikimediaRequest>, Integer> getStatistic(){
		return statistic;
	}

	public void resetStatistic(){
		statistic.clear();
	}
	
}
