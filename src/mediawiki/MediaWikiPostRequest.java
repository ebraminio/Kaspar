package mediawiki;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;

import javat.xml.Document;


public class MediaWikiPostRequest extends PostRequest {

	private MediaWikiConnection con = null;
	
	public MediaWikiPostRequest(MediaWikiConnection c) throws UnsupportedEncodingException {
		super(c.getApihref());
		this.con = c;
		putData("format", "xml");
		if(c.isLoggedIn())
			putData("assert",(c.isBot() ? "bot" : "user"));
		if(c.isBot()){
			putData("bot","");
		//	putData("apihighlimits","1");
		}
		
		for(Entry<String, String> t : c.getCookies().entrySet()){
			putCookie(t.getKey(), t.getValue());
		}
	}
	
	@Override
	public String request() throws IOException {
		String a = super.request();
		if(getResponseHeader().get("Set-Cookie") != null){
			for(String s : getResponseHeader().get("Set-Cookie")){
				con.putCookie(s.split("=")[0], s.split("=")[1].substring(0, s.split("=")[1].indexOf(';')));
			}
		}
		return a;
	}
	
	public Document requestDocument() throws Exception {
		String a = request();
		Document d = Document.load(new ByteArrayInputStream(a.getBytes()));
		if(d.getRootElement().getChildren("error").size() > 0){
			String error = d.getRootElement().getChildren("error").get(0).getAttribute("info").getValue();
			if(error.indexOf("Please try again in a few minutes.") > 0){
				System.out.println("Relogin initiated...");
				con.relogin();
			}
			throw new MediaWikiException("Server Message from "+con.getApihref()+": "+error);
		}
		return d;
	}

}
