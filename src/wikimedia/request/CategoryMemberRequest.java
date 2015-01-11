package wikimedia.request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javat.xml.Document;
import javat.xml.Element;

import javax.xml.stream.XMLStreamException;

import org.xml.sax.SAXException;

import wikimedia.WikimediaConnection;
import wikimedia.WikimediaPostRequest;
import wikimedia.WikimediaRequest;
import wikimedia.info.Article;

public class CategoryMemberRequest extends WikimediaRequest {

	public CategoryMemberRequest(String kat) {
		setProperty("cmtitle", kat);
		setProperty("cmlimit", 5000);
	}
	
	public CategoryMemberRequest(String kat, int namespace) {
		this(kat);
		setProperty("cmnamespace", namespace);
	}

	@Override
	public List<Article> request(WikimediaConnection c) throws IOException,
			XMLStreamException, SAXException {
		ArrayList<Article> r = new ArrayList<>();
		WikimediaPostRequest p = new WikimediaPostRequest(c);
		p.putData(getProperties());
		p.putData("action", "query");
		p.putData("list", "categorymembers");
		Document d = p.requestDocument();
		while(true){
			Element e = d.getRootElement().getChildren("query").get(0).getChildren("categorymembers").get(0);
			for(Element a : e.getChildren("cm")){
				r.add(Article.convert(a));
			}
			if(d.getRootElement().getChildren("continue").size() == 0)
				break;
			p.putData("cmcontinue", d.getRootElement().getChildren("continue").get(0).getAttribute("cmcontinue").getValue());
			d = p.requestDocument();
		}
		
		return r;
	}

}
