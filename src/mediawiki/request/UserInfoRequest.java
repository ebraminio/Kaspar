package mediawiki.request;

import java.util.Properties;

import javat.xml.Attribute;
import javat.xml.Document;
import javat.xml.Element;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;

public class UserInfoRequest extends MediaWikiRequest<Properties> {

	public UserInfoRequest() {
	}

	@Override
	public Properties request(MediaWikiConnection c) throws Exception {
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData("action", "query");
		p.putData("meta", "userinfo");
		p.putData("uiprop", "options");
		
		Document d = p.requestDocument();
		
		Properties prop = new Properties();
		
		Element e = d.getRootElement().getChildren("query").get(0).getChildren("userinfo").get(0).getChildren("options").get(0);
		
		for(Attribute a : e.getAttributes()) {
			prop.put(a.getName(), a.getValue());
		}
		
		return prop;
	}

}
