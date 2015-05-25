package mediawiki.info;

import javat.xml.Element;

public class Article {

	private int pageid;
	private int namespace;
	private String title;
	
	public Article(int pageid, int namespace, String title) {
		setPageid(pageid);
		setNamespace(namespace);
		setTitle(title);
	}
	
	public Article(String string) {
		this(-1,-1,string);
	}

	public static Article convert(Element e){
		Article a = new Article(Integer.parseInt(e.getAttribute("pageid").getValue()), Integer.parseInt(e.getAttribute("ns").getValue()), e.getAttribute("title").getValue());
		return a;
	}
	
	public int getPageid() {
		return pageid;
	}

	public int getNamespace() {
		return namespace;
	}

	public String getTitle() {
		return title;
	}

	public void setPageid(int pageid) {
		this.pageid = pageid;
	}

	public void setNamespace(int namespace) {
		this.namespace = namespace;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isIdentical(Article a){
		if(a == null)
			return false;
		return a.getPageid() == this.pageid || a.getTitle().equals(this.title);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(!(obj instanceof Article))
			return false;
		Article a = (Article) obj;
		return pageid == a.pageid && a.namespace == namespace && a.title.equals(title);
	}
}
