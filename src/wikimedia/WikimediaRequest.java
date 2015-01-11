package wikimedia;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

abstract public class WikimediaRequest {

	private HashMap<String, String> data = new HashMap<>();
	
	abstract public Object request(WikimediaConnection c) throws IOException, XMLStreamException, SAXException;

	public void setProperty(String k, Object v){
		data.put(k, v.toString());
	}
	
	public String getProperty(String k){
		return data.get(k);
	}
	
	public Map<String,String> getProperties(){
		return data;
	}
}
