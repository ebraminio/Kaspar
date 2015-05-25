package mediawiki.request;

import java.util.HashMap;
import java.util.Map;

import javat.xml.Element;
import mediawiki.ContinuingRequest;

public class GetWatchlistRequest extends ContinuingRequest<String> {

	public GetWatchlistRequest(){
		super("watchlistraw", "wr", "wr");
	}
	

	@Override
	protected String parse(Element e) {
		System.out.println(e.getAttributes());
		return null;
	}

	@Override
	protected Map<? extends String, ? extends String> getRequiredParameters() {
		HashMap<String, String> p = new HashMap<>();
		p.put("action", "query");
		p.put("list", "watchlistraw");
		return p;
	}

}
