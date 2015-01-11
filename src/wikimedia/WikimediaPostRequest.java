package wikimedia;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import javat.xml.Document;


public class WikimediaPostRequest extends PostRequest {

	private WikimediaConnection con = null;
	
	public WikimediaPostRequest(WikimediaConnection c) {
		super(c.getApihref());
		this.con = c;
		putData("format", "xml");
		putData("continue","");
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
	
	public Document requestDocument() throws IOException, XMLStreamException, SAXException {
		String a = request();
		return Document.load(new ByteArrayInputStream(a.getBytes()));
	}

}
