package mediawiki.info.wikibase;

import mediawiki.XMLRepresented;
import mediawiki.info.wikibase.snaks.ItemSnak;
import javat.xml.Element;

public class Claim extends XMLRepresented {

	private Property property;
	private Snak<?> entity;
	
	public Claim(Property p, Snak<?> e){
		super();
		setProperty(p);
		setSnak(e);
	}
	
	public Claim(int p, Snak<?> e){
		this(new Property(p), e);
	}
	
	@Deprecated
	public Claim(int p, int e){
		this(new Property(p), new ItemSnak(e));
	}
	
	public Claim(Element e) throws Exception {
		super(e);
	}
	
	
	
	public Property getProperty() {
		return property;
	}
	public void setProperty(Property property) {
		this.property = property;
	}
	public Snak<?> getSnak() {
		return entity;
	}
	public void setSnak(Snak<?> entity) {
		this.entity = entity;
	}

	/**
	 * @param element Element snak / mainsnak
	 * @throws Exception 
	 */
	@Override
	public void convert(Element element) throws Exception { // ab snak / mainsnak
		setProperty(new Property(Integer.parseInt(element.getAttribute("property").getValue().substring(1))));
		Snak<?> e = Snak.createByDatatype(element.getAttribute("datatype").getValue());
		e.convert(element.getChildren("datavalue").get(0));
		setSnak(e);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof Claim))
			return false;
		Claim c = (Claim) obj;
		return property.equals(c.property) && entity.equals(c.entity);
	}
	
	@Override
	public String toString() {
		return super.toString()+"["+property.toString()+"="+entity.toString()+"]";
	}
}
