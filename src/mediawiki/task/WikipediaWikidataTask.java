package mediawiki.task;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiTask;

public abstract class WikipediaWikidataTask extends MediaWikiTask {

	private MediaWikiConnection wikipedia;
	
	public WikipediaWikidataTask(MediaWikiConnection wikidata, MediaWikiConnection wikipedia){
		super(wikidata);
		this.wikipedia = wikipedia;
	}

	public MediaWikiConnection getWikipediaConnection() {
		return wikipedia;
	}

	public MediaWikiConnection getWikidataConnection(){
		return getConnection();
	}

}
