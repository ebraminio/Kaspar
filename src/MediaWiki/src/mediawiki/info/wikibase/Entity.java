package mediawiki.info.wikibase;

import java.util.ArrayList;

public class Entity {

	private ArrayList<Statement> statements = new ArrayList<>();
	
	private TranslatedContent<String> labels = new TranslatedContent<>();
	private TranslatedContent<String> descriptions = new TranslatedContent<>();
	private TranslatedContent<ArrayList<String>> aliases = new TranslatedContent<>();
	
	
	private ArrayList<Sitelink> sitelinks = new ArrayList<>();
	// Wikilinks
	
}
