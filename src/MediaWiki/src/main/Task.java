package main;

import java.util.ArrayList;

import mediawiki.ArticleDenier;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.TranslatedContent;


public class Task {

	public String[] kats;
	public ArticleDenier denier = new ArticleDenier();
	public ArrayList<Claim> attributes = new ArrayList<>();
	public Claim source;
	
	public TranslatedContent<String> description = new TranslatedContent<>();
	
	public boolean isComplete(){
		return kats != null && denier != null && attributes != null && source != null;
	}
	
}
