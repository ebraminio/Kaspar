package mediawiki.info.wikibase;

import mediawiki.info.Project;

public class Sitelink {

	private Project site;
	private String title;
	
	@Deprecated
	public Sitelink(String site, String title){
		setSite(site);
		setTitle(title);
	}
	
	public Sitelink(Project site, String title){
		this.site = site;
		setTitle(title);
	}
	
	public String getSite() {
		return site.getSite();
	}
	public String getTitle() {
		return title;
	}
	public void setSite(String site) {
		this.site.setSite(site);
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public Project getProject(){
		return site;
	}
	
	public String getLanguage(){
		return site.getLanguage();
	}
	
	public String getURLSuffix(){
		return site.getURLSuffix();
	}
	
	public String getURLPrefix(){
		return site.getURLPrefix();
	}
}
