package mediawiki.info.wikibase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class TranslatedContent<T> implements Iterable<Entry<String, T>> {

	private HashMap<String, T> content = new HashMap<>();
	
	public TranslatedContent() {
	}
	
	public TranslatedContent(String lang, T content){
		put(lang, content);
	}
	
	public void put(String language, T c){
		content.put(language, c);
	}
	
	public T get(String language){
		return content.get(language);
	}

	@Override
	public Iterator<Entry<String, T>> iterator() {
		return content.entrySet().iterator();
	}
	
	
}
