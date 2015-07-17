package mediawiki.request;

import java.net.URL;
import javat.xml.Document;
import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;

public class ImageURLRequest extends MediaWikiRequest<URL> {

	public ImageURLRequest(String name, int width) {
		setProperty("titles", name);
		setProperty("iiurlwidth",width);
	}
	
	@Override
	public URL request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("prop", "imageinfo");
		p.putData("iiprop", "url");
		Document d = p.requestDocument();
		return new URL(d.getRootElement().getChildren("query").get(0).getChildren("pages").get(0).getChildren("page").get(0).getChildren("imageinfo").get(0).getChildren("ii").get(0).getAttribute("thumburl").getValue());
	}

}
