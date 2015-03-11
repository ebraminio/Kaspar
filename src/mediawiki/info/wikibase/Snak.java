package mediawiki.info.wikibase;

import javat.xml.Element;
import mediawiki.JSONizable;
import mediawiki.XMLRepresented;
import mediawiki.info.wikibase.snaks.CommonsSnak;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.info.wikibase.snaks.URLSnak;

import org.json.JSONException;


public abstract class Snak<T> extends XMLRepresented implements JSONizable {

	private T value;
	
	public Snak(T value){
		setValue(value);
	}
	
	/**
	 * Herausfinde, ob ein XML-Element genau diesem Snak entspricht
	 * @param e Element auf datavalue-Ebene
	 * @return
	 */
	abstract public boolean equalsXML(Element e);

	abstract public Object toReferenceRepresentation() throws JSONException;
	
	abstract public Object toClaimRepresentation() throws JSONException;
	
	/**
	 * @param element Element auf datavalue-Ebene
	 * @throws Exception 
	 */
	@Override
	abstract public void convert(Element element) throws Exception;
	
	/**
	 * 
	 * @param datatype Datentyp aus datatyp auf snak/mainsnak-Ebene
	 * @return
	 */
	public static Snak<?> createByDatatype(String datatype){
		switch(datatype){
		case "wikibase-item"	:	return new ItemSnak(0);
		case "string"			: 	return new StringSnak(null);
		case "url"				: 	return new URLSnak(null);
		case "time"				: 	return new DateSnak(null);
		case "commonsMedia"		:	return new CommonsSnak(null);
		}
		return null;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	@Override
	public String toString() {
		return super.toString()+"["+getValue().toString()+"]";
	}
}
