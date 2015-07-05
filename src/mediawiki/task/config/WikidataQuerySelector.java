package mediawiki.task.config;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;

import mediawiki.WikidataQuery;
import mediawiki.WikimediaConnection;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Sitelink;
import mediawiki.request.ArticleForNameRequest;
import mediawiki.request.wikibase.GetSitelinkRequest;

public class WikidataQuerySelector implements ACtWPSelector {

	private WikidataQuery wdq;
	private WikimediaConnection wikidata;
	private WikimediaConnection wikipedia;
	
	public WikidataQuerySelector(WikidataQuery wdq, WikimediaConnection wikidata, WikimediaConnection wikipedia){
		this.wdq = wdq;
		this.wikidata = wikidata;
		this.wikipedia = wikipedia;
	}
	
	public WikidataQuerySelector(String query, WikimediaConnection wikidata, WikimediaConnection wikipedia) {
		this(new WikidataQuery(query), wikidata, wikipedia);
	}
	
	public WikidataQuerySelector(String query) {
		wdq = new WikidataQuery(query);
	}
	
	private List<Integer> entities = null;
	
	@Override
	public void prepare(WikimediaConnection wikidata, WikimediaConnection wikipedia) {
		this.wikidata = wikidata;
		this.wikipedia = wikipedia;
	}
	
	@Override
	public void fetch() throws IOException, JSONException {
		if(wikidata == null || wikipedia == null)
			throw new IllegalStateException("please prepare first");
		entities = wdq.request();
	}
	
	@Override
	public boolean hasNext() {
		if(entities == null)
			throw new IllegalStateException("please fetch first");
		return entities.iterator().hasNext();
	}

	@Override
	public Article next() throws Exception {
		if(entities == null)
			throw new IllegalStateException("please fetch first");
		Integer b = entities.iterator().next();
		Sitelink sl = wikidata.request(new GetSitelinkRequest("Q"+b,wikipedia.getProject()));
		return wikipedia.request(new ArticleForNameRequest(sl.getTitle()));
	}

	@Override
	public void close() throws IOException {
		entities = null;
	}

}
