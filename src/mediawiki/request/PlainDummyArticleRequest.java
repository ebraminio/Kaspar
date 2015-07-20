package mediawiki.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiRequest;
import mediawiki.info.Article;

public class PlainDummyArticleRequest extends MediaWikiRequest<List<Article>> {

	private List<String> l;
	
	public PlainDummyArticleRequest(String...names) {
		this(Arrays.asList(names));
	}
	
	public PlainDummyArticleRequest(Collection<String> names) {
		l = new ArrayList<>(names);
	}
	
	@Override
	public List<Article> request(MediaWikiConnection c) throws Exception {
		ArrayList<Article> r = new ArrayList<>(); 
		for(String s : l)
			r.add(c.request(new ArticleForNameRequest(s)));
		return r;
	}

}
