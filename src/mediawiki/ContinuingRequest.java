package mediawiki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javat.xml.Document;
import javat.xml.Element;
import mediawiki.info.Article;

abstract public class ContinuingRequest<T> extends WikimediaRequest<List<T>> {

	private String group;
	private String entry;
	private String prefix;
	
	private int limit = Integer.MAX_VALUE; 
	
	protected ContinuingRequest(String group, String entry, String prefix){
		setGroup(group);
		setEntry(entry);
		setPrefix(prefix);
	}
	
	@Override
	public List<T> request(WikimediaConnection c) throws Exception {
		ArrayList<T> r = new ArrayList<>();
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData(getRequiredParameters());
		Document d = p.requestDocument();
		while(true){
			Element e = d.getRootElement().getChildren("query").get(0).getChildren(getGroup()).get(0);
			for(Element a : e.getChildren(getEntry())){
				r.add(parse(a));
				if(r.size() >= limit)
					return r;
			}
			if(d.getRootElement().getChildren("continue").size() == 0)
				break;
			p.putData(getPrefix()+"continue", d.getRootElement().getChildren("continue").get(0).getAttribute(getPrefix()+"continue").getValue());
			d = p.requestDocument();
		}
		return r;
	}
	
	abstract protected T parse(Element e);
	
	abstract protected Map<? extends String, ? extends String> getRequiredParameters();

	protected String getGroup() {
		return group;
	}

	protected String getEntry() {
		return entry;
	}

	protected String getPrefix() {
		return prefix;
	}

	protected void setGroup(String group) {
		this.group = group;
	}

	protected void setEntry(String entry) {
		this.entry = entry;
	}

	protected void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
