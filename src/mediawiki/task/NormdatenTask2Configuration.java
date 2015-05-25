package mediawiki.task;

import java.util.List;

import mediawiki.WikimediaRequest;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.request.CategoryMemberRequest;
import mediawiki.request.TemplateEmbeddedInRequest;

public class NormdatenTask2Configuration {

	public static final NormdatenTask2Configuration ENWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Category:Pages using authority control with parameters",0),
			new Reference(new Property(143), new ItemSnak(328)),
			"Authority control", "Normdaten"
			);
	public static final NormdatenTask2Configuration FRWIKI = new NormdatenTask2Configuration(
			new TemplateEmbeddedInRequest("Modèle:Autorité",0),
			new Reference(new Property(143), new ItemSnak(8447)),
			"Autorité","Authority control","Normdaten"
			);
	public static final NormdatenTask2Configuration MKWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Категорија:Страници со нормативна контрола со параметри",0),
			new Reference(new Property(143), new ItemSnak(842341)),
			"Нормативна контрола","Authority control","Normdaten"
			);
	
	
	public NormdatenTask2Configuration(WikimediaRequest<List<Article>> r, Reference ref, String...t) {
		this.request = r;
		this.template = t;
		this.reference = ref;
	}
	
	private WikimediaRequest<List<Article>> request;
	private String[] template;
	private Reference reference;
	
	
	public WikimediaRequest<List<Article>> getRequest() {
		return request;
	}

	public String getTemplate() {
		return template[0];
	}
	
	public String[] getTemplates(){
		return template;
	}

	public void setRequest(WikimediaRequest<List<Article>> request) {
		this.request = request;
	}

	public void setTemplate(String template) {
		this.template = new String[]{template};
	}

	public Reference getReference() {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

}
