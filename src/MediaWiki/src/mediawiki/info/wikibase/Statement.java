package mediawiki.info.wikibase;

import java.util.ArrayList;

import mediawiki.XMLRepresented;


import javat.xml.Element;

public class Statement extends XMLRepresented {

	private String id;
	private ArrayList<Reference> references = new ArrayList<>(); 
	private Claim claim;
	
	public Statement(String id) {
		setId(id);
	}
	
	public Statement(Element e) throws Exception {
		super();
		references = new ArrayList<>();
		convert(e);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void addReference(Reference r){
		references.add(r);
	}
	
	public boolean hasReferences(){
		return ! references.isEmpty(); 
	}
	
	public void convert(Element element) throws Exception { // ab claim
		setId(element.getAttribute("id").getValue());
		if(element.getChildren("references").size() > 0){
			for(Element e : element.getChildren("references").get(0).getChildren("reference")){
				addReference(new Reference(e));
			}
		}
		setClaim(new Claim(element.getChildren("mainsnak").get(0)));
	}

	
	public Claim getClaim() {
		return claim;
	}

	public void setClaim(Claim snak) {
		this.claim = snak;
	}

}
