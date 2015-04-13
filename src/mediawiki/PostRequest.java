package mediawiki;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.StringBody;

public class PostRequest {

	private String httpsURL;
	private HashMap<String, String> cookies = new HashMap<>();
	
	private MultipartEntity data = new MultipartEntity(HttpMultipartMode.STRICT);
	
	private Map<String, List<String>> responseheader = null; 
	
	public PostRequest(String url){
		httpsURL = url;
	}
	
	public void putData(String k, AbstractContentBody v){
		data.addPart(k, v);
	}
	
	public void putData(String k, String v) throws UnsupportedEncodingException{
		putData(k, new StringBody(v, "text/plain", Charset.forName("UTF-8")));
	}
	
	public void putData(Map<? extends String, ?> m) throws UnsupportedEncodingException{
		for(Entry<? extends String, ?> e : m.entrySet()){
			if(e.getValue() instanceof java.lang.String)
				putData(e.getKey(), (String)e.getValue());
			else if(e.getValue() instanceof AbstractContentBody)
				putData(e.getKey(), (AbstractContentBody)e.getValue());
			else
				throw new IllegalArgumentException(e.getValue().getClass().getCanonicalName()+" isn't supported as http multipart value");
		}
	}
	
	
	public void putCookie(String k, String v){
		cookies.put(k, v);
	}
	
	public String request() throws IOException{
		
		
		URL myurl = new URL(httpsURL);
		HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
		String cs = "";
		for(Entry<String, String> t : cookies.entrySet()){
			cs += t.getKey()+"="+t.getValue()+"; ";
		}
		con.setRequestProperty("Cookie", cs);
		con.setRequestMethod("POST");
		
		con.setRequestProperty("Content-length", String.valueOf(data.getContentLength())); 
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Content-Type", data.getContentType().getValue()+(data.getContentEncoding() != null ? ";charset="+data.getContentEncoding().getValue() : "")); 
		con.setRequestProperty("User-Agent", "Kaspar 1.0 by Tim Seppelt"); 
		con.setDoOutput(true); 
		con.setDoInput(true); 
		StringBuffer b  = new StringBuffer();
		DataOutputStream output = new DataOutputStream(con.getOutputStream());  
		data.writeTo(output);
		output.close();
		BufferedReader input = new BufferedReader( new InputStreamReader(con.getInputStream(), "UTF-8")); 
		this.responseheader = con.getHeaderFields();
		for( int c = input.read(); c != -1; c = input.read() )
			b.append((char)c ); 
		input.close(); 
		return b.toString(); 
	}
	
	public Map<String, List<String>> getResponseHeader(){
		return responseheader;
	}

}