package datasets.in;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import util.GetRequest;

public class VIAF {

	public static JSONObject getLinks(String viaf) throws IOException, JSONException{
		GetRequest g = new GetRequest("https://viaf.org/viaf/"+viaf+"/justlinks.json");
		String s = g.request();
		return new JSONObject(s);
	}
	
	public static boolean hasEntry(String viaf) throws IOException {
		try{
			new GetRequest("https://viaf.org/viaf/"+viaf+"/justlinks.json").request();
		}catch(FileNotFoundException e){
			return false;
		}
		return true;
	}
}
