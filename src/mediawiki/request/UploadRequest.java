package mediawiki.request;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.entity.mime.content.InputStreamBody;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;

public class UploadRequest extends WikimediaRequest<Object> implements
		ManipulativeRequest {

	
	public UploadRequest(String filename, URL url, String text){
		setProperty("filename", filename);
		setProperty("url", url.toExternalForm());
		setProperty("text", text);
	}
	
	public UploadRequest(String filename, InputStream in, String text, String comment){
		setProperty("filename", filename);
		setProperty("text", text);
		setProperty("file", new InputStreamBody(in, filename));
		setProperty("comment", comment);
	}
	
	
	@Override
	public Object request(WikimediaConnection c) throws Exception {
		String token = c.request(new TokenRequest());
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "upload");
		p.putData("token", token);
		return p.requestDocument();
	}

}
