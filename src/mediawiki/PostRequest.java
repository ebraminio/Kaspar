package mediawiki;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
		con.setRequestProperty("Accept-Charset", "UTF-8");
		con.setRequestProperty("Content-Type","application/x-www-form-urlencoded;charset=utf-8"); 
		con.setRequestProperty("User-Agent", "Kaspar 1.0 by Tim Seppelt"); 
		con.setDoOutput(true); 
		con.setDoInput(true); 
		StringBuffer b  = new StringBuffer();
		DataOutputStream output = new DataOutputStream(con.getOutputStream());  
		output.writeBytes(query);
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

	public Map<String, String> getData(){
		return data;
	}
}
