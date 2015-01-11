package wikimedia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

public class PostRequest {

	private String httpsURL;
	private HashMap<String, String> data = new HashMap<>();
	private HashMap<String, String> cookies = new HashMap<>();
	
	private Map<String, List<String>> responseheader = null; 
	
	public PostRequest(String url){
		httpsURL = url;
	}
	
	public void putData(String k, String v){
		data.put(k, v);
	}
	
	public void putData(Map<? extends String, ? extends String> m){
		data.putAll(m);
	}
	
	public String getData(String k){
		return data.get(k);
	}
	
	public void putCookie(String k, String v){
		cookies.put(k, v);
	}
	
	public String request() throws IOException{
		String query = "";
		
		for(Entry<String, String> t : data.entrySet()){
			if(query!=""){query+="&";}
			try {
				query += t.getKey()+"="+URLEncoder.encode(t.getValue(),"UTF-8");
			} catch (UnsupportedEncodingException e) {}
		}
		URL myurl = new URL(httpsURL);
		HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
		String cs = "";
		for(Entry<String, String> t : cookies.entrySet()){
			cs += t.getKey()+"="+t.getValue()+"; ";
		}
		con.setRequestProperty("Cookie", cs);
		con.setRequestMethod("POST");
		

		con.setRequestProperty("Content-length", String.valueOf(query.length())); 
		con.setRequestProperty("Content-Type","application/x-www-form-urlencoded"); 
		con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0;Windows98;DigExt)"); 
		con.setDoOutput(true); 
		con.setDoInput(true); 

		DataOutputStream output = new DataOutputStream(con.getOutputStream());  
		output.writeBytes(query);
		output.close();
		DataInputStream input = new DataInputStream( con.getInputStream() ); 
		
		this.responseheader = con.getHeaderFields();
		
		StringBuffer b = new StringBuffer();

		for( int c = input.read(); c != -1; c = input.read() ) 
			b.append((char)c ); 
		
		input.close(); 
		
		return b.toString();		
	}
	
	public Map<String, List<String>> getResponseHeader(){
		return responseheader;
	}
}
