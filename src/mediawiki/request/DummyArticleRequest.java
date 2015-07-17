package mediawiki.request;

import java.util.ArrayList;
import java.util.List;

import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiRequest;
import mediawiki.info.Article;

public class DummyArticleRequest extends MediaWikiRequest<List<Article>> {

	private List<Article> l = new ArrayList<>();
	
	public DummyArticleRequest(Article...articles){
		for(Article a : articles) 
			l.add(a);
	}
	
	@Override
	public List<Article> request(MediaWikiConnection c) throws Exception {
		return l;
	}

}
