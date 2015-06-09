package mediawiki;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.StringBody;

abstract public class WikimediaRequest<T extends Object> {

	private HashMap<String, AbstractContentBody> data = new HashMap<>();
	
	abstract public T request(WikimediaConnection c) throws Exception;

	public void setProperty(String k, Object v){
		setProperty(k, v.toString());
	}
	
	public void setProperty(String k, AbstractContentBody v){
		data.put(k, v);
	}
	
	public void setProperty(String k, String v) {
		setProperty(k, new StringBody(v, ContentType.create("text/palin", "UTF-8")));
	}
	
	public Map<String, AbstractContentBody> getProperties(){
		return data;
	}

}
