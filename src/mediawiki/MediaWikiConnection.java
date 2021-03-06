package mediawiki;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import mediawiki.event.RequestEvent;
import mediawiki.event.RequestListener;
import mediawiki.info.Project;
import mediawiki.request.LoginRequest;
import mediawiki.request.LogoutRequest;
import mediawiki.request.ManipulativeRequest;


public class MediaWikiConnection implements Cloneable {

	private String apihref = "";
	
	private LoginRequest loginrequest = null;
	private boolean login = false;
	private boolean bot = false;
	private boolean teststate = false;
	
	private HashMap<String, String> cookies = new HashMap<>();
	private Map<Class<? extends MediaWikiRequest<?>>, Integer> statistic = new HashMap<>();
	private final Object editsynchronizer = new Object();
	
	private ArrayList<RequestListener> listener = new ArrayList<>();
	private GlobalWikimediaConnection globalcon = null;
	
	private long editInterval = 0L;
	private Date lastEdit = null;
	
	public MediaWikiConnection(String apihref){
		setApihref(apihref);
	}
	
	public MediaWikiConnection(String language, String wikiname){
		this("https://"+language+"."+wikiname+"/w/api.php");
	}
	
	public MediaWikiConnection(Project s){
		this(s.getURLPrefix(),s.getURLSuffix());
	}
	
	public <T> T request(MediaWikiRequest<T> r) throws Exception{
		if(r instanceof LoginRequest)
			setLoginRequest((LoginRequest)r);
		T o = null;
		if(r instanceof ManipulativeRequest){
			if(isTestState()){
				System.out.println("test state edit: "+r.toString());
				return null;
			}else{
				if(editInterval != 0L && lastEdit != null && (new Date().getTime() < lastEdit.getTime()+editInterval)){
					Thread.currentThread().sleep(lastEdit.getTime()+editInterval-new Date().getTime());
				}
				synchronized(editsynchronizer){
					o = r.request(this);
				}
				lastEdit = new Date();
			}
		}else{
			o = r.request(this);
		}
		log(r);
		fireRequestEvent(new RequestEvent(r));
		return o;
	}

	public String getApihref() {
		return apihref;
	}

	public void setApihref(String apihref) {
		this.apihref = apihref;
	}

	private void log(MediaWikiRequest<?> r){
		if(statistic.containsKey(r.getClass())){
			statistic.put((Class<? extends MediaWikiRequest<?>>) r.getClass(), statistic.get(r.getClass())+1);
		}else{
			statistic.put((Class<? extends MediaWikiRequest<?>>) r.getClass(), 1);
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
	
	public Map<Class<? extends MediaWikiRequest<?>>, Integer> getStatistic(){
		return statistic;
	}

	public void resetStatistic(){
		statistic.clear();
	}

	public void addRequestListener(RequestListener r){
		listener.add(r);
	}
	
	public int getStatisticOf(Class<? extends MediaWikiRequest<?>> s){
		return statistic.containsKey(s) ? statistic.get(s) : 0;
	}
	
	protected void fireRequestEvent(RequestEvent r){
		for(RequestListener rl : listener)
			rl.requestPerformed(r);
	}

	public int getEditCount(){
		int i = 0;
		for(Entry<Class<? extends MediaWikiRequest<?>>, Integer> e : statistic.entrySet()){
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

	public void setEditInterval(long milliseconds){
		editInterval = milliseconds;
	}
	
	public long getEditInterval(){
		return editInterval;
	}
	
	public Project getProject() throws MalformedURLException{
		return Project.forAPIhref(getApihref());
	}
}
