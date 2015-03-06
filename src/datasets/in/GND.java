package datasets.in;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import util.GetRequest;
import javat.xml.Document;
import javat.xml.Element;

public class GND {

	public static Element getGNDEntry(String gnd) throws IOException, XMLStreamException, SAXException{
		GetRequest g = new GetRequest("http://d-nb.info/gnd/"+gnd+"/about/lds");
		String s = g.request();
		Document d = Document.load(new ByteArrayInputStream(s.getBytes()));
		return d.getRootElement().getChildren("Description").get(0);
		
	}
	
}
