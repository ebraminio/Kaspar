package mediawiki.request;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaRequest;
import mediawiki.info.Article;

public class RecursiveCategoryMemberRequest extends
		WikimediaRequest<Set<Article>> {

	private String category;
	private boolean includecats = false;
	private Integer namespace = null;
	
	public RecursiveCategoryMemberRequest(String cat){
		category = cat;
	}
	
	public RecursiveCategoryMemberRequest(String cat, int namespace, boolean includecats){
		category = cat;
		this.includecats = includecats;
		this.namespace = namespace;
	}
	
	@Override
	public Set<Article> request(WikimediaConnection c) throws Exception {
		return handleSubcategory(c, category);
	}
	
	private Set<Article> handleSubcategory(WikimediaConnection c, String cat) throws Exception{
		HashSet<Article> h = new HashSet<>();
		List<Article> l = c.request(new CategoryMemberRequest(cat));
		for(Article a : l){
			if(a.getNamespace() == 14){
				if(includecats)
					h.add(a);
				h.addAll(handleSubcategory(c, a.getTitle()));
			}else if(namespace == null || a.getNamespace() == namespace){
				h.add(a);
			}
		}
		return h;
	}
	

}
