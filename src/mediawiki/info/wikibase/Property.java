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
}
