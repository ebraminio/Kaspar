package mediawiki.task;

import mediawiki.WikimediaConnection;
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

	public WikimediaConnection getWikidataConnection(){
		return getConnection();
	}

}
