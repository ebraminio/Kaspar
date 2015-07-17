package mediawiki.task.config;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import mediawiki.MediaWikiConnection;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Sitelink;
import mediawiki.request.ArticleForNameRequest;
import mediawiki.request.wikibase.GetSitelinkRequest;

public class AutolistSelector implements ACtWPSelector {

	private String url;
	
	public AutolistSelector(URL u){
		url = u.toExternalForm();
	}
	
	public AutolistSelector(String u){
		url = u;
	}
	
	private Scanner s = null;
	
	private MediaWikiConnection wikidata;
	private MediaWikiConnection wikipedia;
	
	@Override
	public void prepare(MediaWikiConnection wikidata,
			MediaWikiConnection wikipedia) {
		this.wikidata = wikidata;
		this.wikipedia = wikipedia;
	}

	@Override
	public boolean hasNext() throws Exception {
		if(s == null)
			throw new IllegalStateException("please fetch first");
		return s.hasNextLine();
	}

	@Override
	public Article next() throws Exception {
		if(s == null)
			throw new IllegalStateException("please fetch first");
		Sitelink sl = wikidata.request(new GetSitelinkRequest(s.nextLine(), wikipedia.getProject()));
		return wikipedia.request(new ArticleForNameRequest(sl.getTitle()));
	}

	@Override
	public void fetch() throws Exception {
		if(wikidata == null || wikipedia == null)
			throw new IllegalStateException("please prepare first");
		s = new Scanner(new URL(url).openStream());
	}

	@Override
	public void close() throws IOException {
		s.close();
	}

	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
