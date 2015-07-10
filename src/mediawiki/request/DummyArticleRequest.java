package mediawiki.request;

import java.util.ArrayList;
import java.util.List;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaRequest;
import mediawiki.info.Article;

public class DummyArticleRequest extends WikimediaRequest<List<Article>> {

	private List<Article> l = new ArrayList<>();
	
	public DummyArticleRequest(Article...articles){
		for(Article a : articles) 
			l.add(a);
	}
	
	@Override
	public List<Article> request(WikimediaConnection c) throws Exception {
		return l;
	}

}
