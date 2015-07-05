package mediawiki.info.wikibase;

public class Property {

	private int id;
	
	public Property(int id){
		this.setID(id);
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "P"+id;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof Property))
			return false;
		Property p = (Property) obj;
		return id == p.id;
	}
}
