package wikimedia.info.wikibase;

import javat.xml.Element;

public class Claim {

	private String id;
	
	public Claim(String id) {
		setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	public static Claim convert(Element element) {
		return new Claim(element.getAttribute("id").getValue());
	}

}
