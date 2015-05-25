package mediawiki.request;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.ContinuingRequest;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;

public class ImageURLRequest extends WikimediaRequest<URL> {

	public ImageURLRequest(String name, int width) {
		setProperty("titles", name);
		setProperty("iiurlwidth",width);
	}
	
	@Override
	public URL request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("prop", "imageinfo");
		p.putData("iiprop", "url");
		Document d = p.requestDocument();
		return new URL(d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("imageinfo").get(0).getChildren("ii").get(0).getAttribute("thumburl").getValue());
	}

}
