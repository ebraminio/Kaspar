package wikimedia;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

public class WikimediaConnection {

	private String wikiname = "";
	private String apihref = "";
	private String language = "";
	
	private HashMap<String, String> cookies = new HashMap<>();
	
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

	public Object request(WikimediaRequest r) throws IOException, XMLStreamException, SAXException{
		return r.request(this);
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

	
}
