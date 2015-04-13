package mediawiki.task;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaRequest;
import mediawiki.WikimediaTask;

public abstract class WikipediaWikidataTask extends WikimediaTask {

	private WikimediaConnection wikipedia;
	
	public WikipediaWikidataTask(WikimediaConnection wikidata, WikimediaConnection wikipedia){
		super(wikidata);
		this.wikipedia = wikipedia;
	}

	public WikimediaConnection getWikipediaConnection() {
		return wikipedia;
	}

}
