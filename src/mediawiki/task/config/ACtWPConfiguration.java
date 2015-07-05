package mediawiki.task.config;

import mediawiki.info.Project;

public class ACtWPConfiguration {

	public static final ACtWPConfiguration ENWIKI = new ACtWPConfiguration(
			new SimpleAutolistSelector(new Project("enwiki"), "Wikipedia articles with authority control information", 227, 214, 224, 213, 496, 906, 269, 268, 651, 245, 434, 409, 349, 1015, 1053, 650),
			new TokenPositioner("{{Persondata", "{{DEFAULTSORT:", "[[Category:"), 
			"Authority control"
		);
	
	public static final ACtWPConfiguration DAWIKI = new ACtWPConfiguration(
			new SimpleAutolistSelector(new Project("dawiki"), "Artikler med autoritetsdata", 214, 244, 213, 496, 227, 906, 269, 268, 651, 1053, 1015, 245, 902, 886, 434, 549, 409, 349, 1048, 691, 640, 396, 947, 428, 1222, 1223, 1157, 950, 271, 781, 1248, 650),
			new TokenPositioner("{{Persondata", "[[Kategori:"), 
			"Autoritetsdata"
		);
	
	public static final ACtWPConfiguration MKWIKI = new ACtWPConfiguration(
			new SimpleAutolistSelector(new Project("mkwiki"), "Википедија:Статии со нормативна контрола", 214, 244, 213, 496, 227, 906, 269, 268, 651, 1053, 1015, 245, 902, 886, 434, 549, 409, 349, 1048, 691, 640, 396, 947, 428, 1222, 1223, 1157, 950, 271, 781, 1248, 650),
			new TokenPositioner("{{Лични податоци", "{{Persondata", "{{DEFAULTSORT:", "[[Категорија:", "[[Category:"), 
			"Нормативна контрола"
		);
	
	private ACtWPSelector selector;
	private ACtWPPositioner positioner;
	private String template;
	
	public ACtWPConfiguration(ACtWPSelector selector, ACtWPPositioner positioner, String template){
		setPositioner(positioner);
		setSelector(selector);
		setTemplate(template);
	}
	
	public ACtWPSelector getSelector() {
		return selector;
	}
	public ACtWPPositioner getPositioner() {
		return positioner;
	}
	public void setSelector(ACtWPSelector selector) {
		this.selector = selector;
	}
	public void setPositioner(ACtWPPositioner positioner) {
		this.positioner = positioner;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
