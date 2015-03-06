package mediawiki.info.wikibase;

import java.util.ArrayList;
import java.util.Iterator;

import mediawiki.XMLRepresented;

import javat.xml.Element;

public class Reference extends XMLRepresented implements Iterable<Claim>{

	private String hash;
	private ArrayList<Claim> claims = new ArrayList<>();
	
	public Reference(){
		
	}
	
	public Reference(String hash){
		setHash(hash);
	}

	public Reference(Element e) throws Exception {
		super();
		claims = new ArrayList<>();
		convert(e);
	}

	public Reference(Property p, Snak<?> s){
		this(new Claim(p,s));
	}
	
	public Reference(Claim a) {
		claims = new ArrayList<>();
		claims.add(a);
	}

	public String getHash() {
		return hash;
	}


	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public void addClaim(Claim a){
		claims.add(a);
	}
	
	public void convert(Element e) throws Exception{ // ab reference
		setHash(e.getAttribute("hash").getValue());
		for(Element p : e.getChildren("snaks").get(0).getChildren("property")){
			Claim a = new Claim(p.getChildren("snak").get(0));
			addClaim(a);
		}
	}

	@Override
	public Iterator<Claim> iterator() {
		return claims.iterator();
	}
}
