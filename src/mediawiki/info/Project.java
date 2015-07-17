package mediawiki.info;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;

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
		return site.replaceAll(Matcher.quoteReplacement(getProject()), "");
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
	
	public static Project forAPIhref(String api) throws MalformedURLException{
		api = new URL(api).getHost();
		api = api.replaceAll("\\.org", "");
		if(api.equals("wikidata"))
			return new Project("wikidatawiki");
		if(api.equals("commons.wikimedia"))
			return new Project("commonswiki");
		if(api.endsWith("wikipedia"))
			api = api.replaceAll("wikipedia", "wiki");
		api = api.replaceAll("\\.", "");
		return new Project(api);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof Project))
			return false;
		return site.equals(((Project)obj).site);
	}

}
