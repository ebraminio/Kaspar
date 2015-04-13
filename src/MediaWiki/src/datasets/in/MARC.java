package datasets.in;

import java.util.ArrayList;
import java.util.HashMap;

import javat.xml.Element;

public class MARC {

	private Element e;
	
	public MARC(Element element){
		e = element;
	}
	
	public ArrayList<HashMap<String, ArrayList<String>>> getDatafield(String tag){
		ArrayList<HashMap<String, ArrayList<String>>> h = new ArrayList<>();
		for(Element ds : e.getChildren("datafield")){
			if(ds.getAttribute("tag").getValue().equals(tag)){
				HashMap<String, ArrayList<String>> hs = new HashMap<>();
				for(Element sf: ds.getChildren("subfield")){
					ArrayList<String> s = new ArrayList<>();
					if( hs.containsKey(sf.getAttribute("code").getValue())){
						s = hs.get(sf.getAttribute("code").getValue());
					}
					s.add(sf.getText());
					hs.put(sf.getAttribute("code").getValue(), s);
				}
				h.add(hs);
			}
		}
		return h;
	}
	
	public String getSubfield(String tag, int index1, String code, int index2){
		return getDatafield(tag).get(index1).get(code).get(index2);
	}
}
