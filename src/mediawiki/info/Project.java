package mediawiki.info;

public class Project {

	private String site;
	
	public Project(String site) {
		this.site = site;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getProject(){
		for(String s : new String[]{"wikibooks","wikiquote","wikisource","wikinews","wikivoyage","wikidatawiki","commonswiki","wiki"}){
			if(site.endsWith(s))
				return s;
		}
		return null;
	}
	
	public String getLanguage(){
		return site.replaceAll(getProject(), "");
	}
	
	public String getURLSuffix(){
		if(getProject().equals("wikidatawiki"))
			return "wikidata.org";
		if(getProject().equals("commonswiki"))
			return "wikimedia.org";
		if(getProject().equals("wiki"))
			return "wikipedia.org";
		return getProject()+".org";
	}
	
	public String getURLPrefix(){
		if(getProject().equals("wikidatawiki"))
			return "www";
		if(getProject().equals("commonswiki"))
			return "commons";
		return getLanguage().replace('_', '-');
	}
	
	@Override
	public String toString() {
		return getSite();
	}
}
