package mediawiki.request.wikibase;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiPostRequest;
import mediawiki.MediaWikiRequest;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class ModifierAliasesRequest extends MediaWikiRequest implements ManipulativeRequest{

	public ModifierAliasesRequest(String base, String method, String language, String...aliases){
		this(base, method, language, "", aliases);
	}
	
	public ModifierAliasesRequest(String base, String method, String language, String summary, String...aliases){
		switch(method){
		case "set":
		case "add":
		case "remove":
			break;
		default:
			throw new IllegalArgumentException("Method "+method+" isn't supported");
		}
		setProperty("id", base);
		setProperty("language", language);
		setProperty("summary", summary);
		String als = "";
		for(String l : aliases){
			als += "|"+l;
		}
		als = als.substring(1);
		setProperty(method, als);		
	}
	
	@Override
	public Object request(MediaWikiConnection c) throws Exception {
		String token = (String) c.request(new TokenRequest());
		MediaWikiPostRequest p = new MediaWikiPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetaliases");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
