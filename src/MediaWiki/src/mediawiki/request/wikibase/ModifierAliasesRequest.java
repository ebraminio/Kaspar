package mediawiki.request.wikibase;

import java.nio.file.WatchEvent.Modifier;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.request.ManipulativeRequest;
import mediawiki.request.TokenRequest;

public class ModifierAliasesRequest extends WikimediaRequest implements ManipulativeRequest{

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
	public Object request(WikimediaConnection c) throws Exception {
		String token = (String) c.request(new TokenRequest());
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbsetaliases");
		p.putData("token", token);
		p.requestDocument();
		return null;
	}

}
