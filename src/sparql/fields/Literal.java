package sparql.fields;

import javat.xml.Element;
import sparql.Field;

public class Literal extends Field<String> {

	
	public Literal(Element e) throws Exception {
		super(e);
	}

	@Override
	public void convert(Element element) throws Exception {
		setValue(element.getText());
	}

}
