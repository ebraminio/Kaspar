package mediawiki.event;

import mediawiki.WikimediaRequest;

public class RequestEvent {

	private WikimediaRequest<?> request;
	
	public RequestEvent(WikimediaRequest<?> r){
		request = r;
	}

	public WikimediaRequest<?> getRequest() {
		return request;
	}

}
