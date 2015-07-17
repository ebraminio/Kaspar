package mediawiki.event;

import mediawiki.MediaWikiRequest;

public class RequestEvent {

	private MediaWikiRequest<?> request;
	
	public RequestEvent(MediaWikiRequest<?> r){
		request = r;
	}

	public MediaWikiRequest<?> getRequest() {
		return request;
	}

}
