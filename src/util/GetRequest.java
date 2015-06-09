package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class GetRequest {

	private URL url;
	
	public GetRequest(URL u){
		url = u;
	}
	
	public GetRequest(String s) throws MalformedURLException{
		url = new URL(s);
	}
	
	public String request() throws IOException{

		URLConnection con = url.openConnection();
        con.setRequestProperty("Accept-Charset", "UTF-8");
        
        InputStream is =con.getInputStream();
        
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        
        String line = null;
        StringBuffer buffer = new StringBuffer();
        while ((line = br.readLine()) != null) {
        	buffer.append(line+"\n");
        }
        return buffer.toString().trim();
	}
	
	public URL detectRedirect() throws IOException {
		URL u = url;
		HttpURLConnection ucon;
		do{
			ucon = (HttpURLConnection) u.openConnection();
			ucon.setInstanceFollowRedirects(false);
			if(ucon.getHeaderField("Location") == null)
				break;
			u = new URL(u,ucon.getHeaderField("Location"));
		}while(ucon.getResponseCode() != 200);
		return u;
	}
}
