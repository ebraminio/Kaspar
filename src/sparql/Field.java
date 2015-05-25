package sparql;

import sparql.fields.Literal;
import sparql.fields.URI;
import javat.xml.Element;
import mediawiki.XMLRepresented;

public abstract class Field<T> extends XMLRepresented {

	private T value;
	
	public Field(Element e) throws Exception{
		convert(e);
	}
	
	public static Field parseField(Element e) throws Exception{
		switch(e.getName()){
		case "uri" : return new URI(e);
		case "literal" : return new Literal(e);
		}
		return null;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return getClass().getName()+"["+getValue().toString()+"]";
	}
	
}
