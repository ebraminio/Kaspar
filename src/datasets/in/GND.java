package datasets.in;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.stream.XMLStreamException;

import mediawiki.info.wikibase.WikibaseDate;

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
	
	public static MARC getMARCEntry(String gnd) throws IOException, XMLStreamException, SAXException{
		GetRequest g = new GetRequest("http://d-nb.info/gnd/"+gnd+"/about/marcxml");
		String s = g.request();
		Document d = Document.load(new ByteArrayInputStream(s.getBytes()));
		return new MARC(d.getRootElement());
	}
	
	public static boolean hasEntry(String gnd) throws IOException, XMLStreamException, SAXException{
		try{
			getGNDEntry(gnd);
		}catch(FileNotFoundException e){
			return false;
		}
		return true;
	}

	public static WikibaseDate parseWikibaseDate(String date) throws ParseException {
		WikibaseDate wbd = null;
		if(date.matches("\\d\\d\\d\\d")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			wbd = new WikibaseDate(sdf.parse(date), 0, 0, 0, WikibaseDate.ONE_YEAR);
		}else if(date.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			wbd = new WikibaseDate(sdf.parse(date), 0, 0, 0, WikibaseDate.ONE_DAY);
		}else {
			throw new ParseException("unable to parse WikibaseDate",0);
		}
		return wbd;
	}
	
}
