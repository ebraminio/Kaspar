package mediawiki.info.wikibase;

public class Sitelink {

	private String site;
	private String title;
	
	public Sitelink(String site, String title){
		setSite(site);
		setTitle(title);
	}
	
	public String getSite() {
		return site;
	}
	public String getTitle() {
		return title;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public void setTitle(String title) {
		this.title = title;
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
}
