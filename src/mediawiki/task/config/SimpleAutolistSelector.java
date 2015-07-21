package mediawiki.task.config;

import java.net.URLEncoder;
import java.nio.charset.Charset;

import util.Util;

import mediawiki.info.Project;
import mediawiki.info.wikibase.Property;

public class SimpleAutolistSelector extends AutolistSelector {

	public SimpleAutolistSelector(String u) {
		super(u);
		
	}
	
	public SimpleAutolistSelector(Project p, String cat, Integer...claims){
		this("https://tools.wmflabs.org/autolist/index.php?language=" + URLEncoder.encode(p.getURLPrefix()) + 
				"&project=" + URLEncoder.encode(p.getURLSuffix().replaceAll("\\.org", "")) + 
				"&category=" + URLEncoder.encode(cat) +
				"&depth=12&wdq="+URLEncoder.encode("(CLAIM[" + Util.implode(claims,"] OR CLAIM[")+"]) AND LINK["+p.toString()+"]") + 
				"&statementlist=&run=Run&mode_manual=or&mode_cat=not&mode_wdq=and&mode_find=or&chunk_size=10000&download=1");
	}
	
	private static Integer[] propertyArrayToIntegerArray(Property[] ps) {
		Integer[] r = new Integer[ps.length];
		for(int i = 0; i < ps.length; i++) {
			r[i] = ps[i].getID();
		}
		return r;
	}
	
}
