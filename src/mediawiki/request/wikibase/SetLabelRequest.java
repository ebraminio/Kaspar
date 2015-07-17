package mediawiki.request.wikibase;

import java.util.Map.Entry;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.info.wikibase.TranslatedContent;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class SetLabelRequest extends MediaWikiRequest implements ManipulativeRequest {

	private TranslatedContent<String> labels;
	
	public SetLabelRequest(String base, String language, String label){
		this(base, language, label, "");
	}
	
	public SetLabelRequest(String base, String language, String label, String summary){
		this(base, new TranslatedContent<String>(language, label), summary);
	}
	
	public SetLabelRequest(String base, TranslatedContent<String> labels, String summary){
		setProperty("id", base);
		setProperty("summary", summary);
		this.labels = labels;
	}
	
	
	@Override
	public Object request(MediaWikiConnection c) throws Exception {
		for(Entry<String, String> e : labels){
			String token = (String) c.request(new TokenRequest());
			MediaWikiPostRequest p = new MediaWikiPostRequest(c);
			p.putData(getProperties());
			p.putData("action", "wbsetlabel");
			p.putData("language", e.getKey());
			p.putData("value", e.getValue());
			p.putData("token", token);
			p.requestDocument();
		}
		return null;
	}

}
