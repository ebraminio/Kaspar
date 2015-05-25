package sparql;

import java.util.HashMap;

import sparql.fields.Literal;
import sparql.fields.URI;

import javat.xml.Element;
import mediawiki.XMLRepresented;

public class Result extends XMLRepresented {

	private HashMap<String, Field> bindings = new HashMap<>();
	
	public Result(Element e) throws Exception{
		convert(e);
	}
	
	@Override
	public void convert(Element element) throws Exception {
		for(Element b : element.getChildren("binding")){
			bindings.put(b.getAttribute("name").getValue(), Field.parseField(b.getChildren().get(0)));
		}
	}
	
	public Field<?> getField(String name){
		return bindings.get(name);
	}
	
	public URI getURI(String name){
		return (URI)bindings.get(name);
	}
	
	public Literal getLiteral(String name){
		return (Literal) bindings.get(name);
	}

}
