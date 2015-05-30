package mediawiki;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mediawiki.event.RequestEvent;
import mediawiki.event.RequestListener;
import mediawiki.info.Project;
import mediawiki.request.LoginRequest;
import mediawiki.request.LogoutRequest;
import mediawiki.request.ManipulativeRequest;


public class WikimediaConnection implements Cloneable {

	private String wikiname = "";
	private String apihref = "";
	private String language = "";
	
	private LoginRequest loginrequest = null;
	private boolean login = false;
	private boolean bot = false;
	private boolean teststate = false;
	
	private HashMap<String, String> cookies = new HashMap<>();
	private Map<Class<? extends WikimediaRequest<?>>, Integer> statistic = new HashMap<>();
	private final Object editsynchronizer = new Object();
	
	private ArrayList<RequestListener> listener = new ArrayList<>();
	private GlobalWikimediaConnection globalcon = null;
	
	public WikimediaConnection(String apihref){
		setApihref(apihref);
	}
	
	public WikimediaConnection(String language, String wikiname){
		this("https://"+language+"."+wikiname+"/w/api.php");
		setWikiname(wikiname);
		setLanguage(language);
	}
	
	public WikimediaConnection(Project s){
		this(s.getURLPrefix(),s.getURLSuffix());
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

	public <T> T request(WikimediaRequest<T> r) throws Exception{
		if(r instanceof LoginRequest)
			setLoginRequest((LoginRequest)r);
		T o = null;
		if(r instanceof ManipulativeRequest){
			if(isTestState()){
				System.out.println("test state edit: "+r.toString());
				return null;
			}else{
				synchronized(editsynchronizer){
					o = r.request(this);
				}
			}
		}else{
			o = r.request(this);
		}
		log(r);
		fireRequestEvent(new RequestEvent(r));
		return o;
	}
	
	private void log(WikimediaRequest<?> r){
		if(statistic.containsKey(r.getClass())){
			statistic.put((Class<? extends WikimediaRequest<?>>) r.getClass(), statistic.get(r.getClass())+1);
		}else{
			statistic.put((Class<? extends WikimediaRequest<?>>) r.getClass(), 1);
		}
	}
	
	public void putCookie(String k, String v) {
		cookies.put(k, v);
		if(isGloballyConnected())
			getGlobalConnection().putGlobalCookie(k, v);
	}
	
	public void putCookies(Map<String, String> m){
		cookies.putAll(m);
		if(isGloballyConnected())
			getGlobalConnection().putGlobalCookies(m);
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
		if(isGloballyConnected())
			getGlobalConnection().setLoggedIn(l);
	}

	public void setLoginRequest(LoginRequest loginrequest) {
		this.loginrequest = loginrequest;
	}
	
	public Object relogin() throws Exception {
		return request(getLoginRequest());
	}
	
	public Map<Class<? extends WikimediaRequest<?>>, Integer> getStatistic(){
		return statistic;
	}

	public void resetStatistic(){
		statistic.clear();
	}

	public void addRequestListener(RequestListener r){
		listener.add(r);
	}
	
	public int getStatisticOf(Class<? extends WikimediaRequest<?>> s){
		return statistic.containsKey(s) ? statistic.get(s) : 0;
	}
	
	protected void fireRequestEvent(RequestEvent r){
		for(RequestListener rl : listener)
			rl.requestPerformed(r);
	}

	public int getEditCount(){
		int i = 0;
		for(Entry<Class<? extends WikimediaRequest<?>>, Integer> e : statistic.entrySet()){
			for(Class c : e.getKey().getInterfaces()){
				if(c.equals(ManipulativeRequest.class)){
					i += e.getValue();
					break;
				}
			}
		}
		return i;
	}

	public void setTestState(boolean test){
		teststate = test;
	}
	
	public boolean isTestState(){
		return teststate;
	}

	
	public GlobalWikimediaConnection getGlobalConnection() {
		return globalcon;
	}

	public void setGlobalConnection(GlobalWikimediaConnection globalcon) {
		this.globalcon = globalcon;
	}
	public boolean isGloballyConnected(){
		return getGlobalConnection() != null;
	}
	
}
