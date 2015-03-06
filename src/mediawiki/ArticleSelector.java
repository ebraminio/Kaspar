package mediawiki;

import mediawiki.info.Article;

public class ArticleSelector implements ArticleHandler {

	private int namespace;
	private ArticleDenier denier;
	
	public ArticleSelector(){
		
	}
	
	public ArticleSelector(int ns, ArticleDenier d){
		this();
		setNamespace(ns);
		setDenier(d);
	}
	
	public int getNamespace() {
		return namespace;
	}
	public ArticleDenier getDenier() {
		return denier;
	}
	public void setNamespace(int namespace) {
		this.namespace = namespace;
	}
	public void setDenier(ArticleDenier denier) {
		this.denier = denier;
	}

	@Override
	public boolean isDeniable(Article a) {
		if(a.getNamespace() != namespace)
			return true;
		if(denier.isDeniable(a))
			return true;
		return false;
	}
	
	
}
