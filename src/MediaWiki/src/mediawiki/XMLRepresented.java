package mediawiki;

import javat.xml.Element;

abstract public class XMLRepresented {

	public XMLRepresented(Element e) throws Exception{
		convert(e);
	}
	public XMLRepresented(){
		
	}
	
	abstract public void convert(Element element) throws Exception;
}
