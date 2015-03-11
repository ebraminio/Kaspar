package mediawiki.request.wikibase;

import java.util.ArrayList;

import javat.xml.Document;
import javat.xml.Element;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaPostRequest;
import mediawiki.WikimediaRequest;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Snak;
import mediawiki.info.wikibase.Statement;


public class GetSpecificStatementRequest extends WikimediaRequest<ArrayList<Statement>> {

	private Snak<?> entity = null;
	
	public GetSpecificStatementRequest(String base, Property property) {
		setProperty("entity", base);
		setProperty("property", property.toString());
	}
	
	public GetSpecificStatementRequest(String base, Claim attribute) {
		this(base, attribute.getProperty());
		setEntity(attribute.getSnak());
	}
	
	
	@Override
	public ArrayList<Statement> request(WikimediaConnection c) throws Exception {
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "wbgetclaims");
		Document d = p.requestDocument();
		
		ArrayList<Statement> r = new ArrayList<>();
		if(d.getRootElement().getChildren("claims").get(0).getChildren("property").size() == 0){
			return r;
		}
		
		for(Element e : d.getRootElement().getChildren("claims").get(0).getChildren("property").get(0).getChildren("claim")){
			Element value = e.getChildren("mainsnak").get(0).getChildren("datavalue").get(0);
			if(getEntity() != null && ! getEntity().equalsXML(value))
				continue;
			r.add(new Statement(e));
		}
		return r;
	}

	public Snak<?> getEntity() {
		return entity;
	}

	public void setEntity(Snak<?> entity) {
		this.entity = entity;
	}

}
