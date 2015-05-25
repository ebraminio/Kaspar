package sparql.fields;

import java.util.regex.Matcher;

import javat.xml.Element;
import sparql.Field;

public class URI extends Field<java.net.URI>{

	public URI(Element e) throws Exception {
		super(e);
	}

	@Override
	public void convert(Element element) throws Exception {
		setValue(new java.net.URI(element.getText()));
	}

	public String getSuffix(String prefix){
		return getValue().toString().replaceFirst(Matcher.quoteReplacement(prefix), "");
	}
	
}
