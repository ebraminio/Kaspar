package mediawiki;

import java.util.ArrayList;

import mediawiki.info.Article;


public class ArticleDenier implements ArticleHandler {

	private ArrayList<Article> list = new ArrayList<>();
	
	public ArticleDenier() {
	}
	
	public ArticleDenier(Article...articles) {
		for(Article a : articles){
			register(a);
		}
	}
	
	public void register(Article a){
		list.add(a);
	}
	
	public void register(String name){
		registerByName(name);
	}
	
	public void register(int pageid){
		registerById(pageid);
	}
	
	public void register(Article... a){
		for(Article b : a)
			register(a);
	}
	
	public void register(String... name){
		registerByName(name);
	}
	
	public void register(int... pageid){
		for(int id : pageid)
			register(id);
	}
	
	public void registerById(Article a){
		a.setTitle(null);
		list.add(a);
	}
	
	public void registerById(int pageid){
		registerById(new Article(pageid, -1, null));
	}
	
	public void registerByName(Article a){
		a.setPageid(-1);
		list.add(a);
	}
	
	public void registerByName(String name){
		registerByName(new Article(-1, -1, name));
	}
	
	public void registerByName(String...strings){
		for(String n : strings)
			registerByName(n);
	}
	
	public boolean isDeniable(Article a){
		for(Article a2 : list)
			if(a2.isIdentical(a))
				return true;
		return false;
	}

	public int getRulesCount(){
		return list.size();
	}

}
