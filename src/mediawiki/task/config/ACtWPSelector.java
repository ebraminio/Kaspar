package mediawiki.task.config;

import java.io.Closeable;

import mediawiki.MediaWikiConnection;
import mediawiki.info.Article;

public interface ACtWPSelector extends Closeable {

	public void prepare(MediaWikiConnection wikidata, MediaWikiConnection wikipedia);
	
	public boolean hasNext() throws Exception;
	
	public Article next() throws Exception;
	
	public void fetch() throws Exception;
}
