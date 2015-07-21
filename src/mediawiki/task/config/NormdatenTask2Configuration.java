package mediawiki.task.config;

import java.util.List;

import mediawiki.MediaWikiRequest;
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
	public static final NormdatenTask2Configuration ENWIKI2 = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Category:VIAF not on Wikidata",0),
			new Reference(new Property(143), new ItemSnak(328)),
			"Authority control", "Normdaten"
			);
	public static final NormdatenTask2Configuration FRWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Catégorie:Page utilisant le modèle Autorité avec un paramètre local",0),
			new Reference(new Property(143), new ItemSnak(8447)),
			"Autorité","Authority control","Normdaten"
			);
	public static final NormdatenTask2Configuration MKWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Категорија:Страници со нормативна контрола со параметри",0),
			new Reference(new Property(143), new ItemSnak(842341)),
			"Нормативна контрола","Authority control","Normdaten"
			);
	public static final NormdatenTask2Configuration DAWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Kategori:Sider med autoritetsdata med parametre",0),
			new Reference(new Property(143), new ItemSnak(181163)),
			"Autoritetsdata","Normdata","Authority control","Normdaten"
			);
	public static final NormdatenTask2Configuration DEWIKI = new NormdatenTask2Configuration(
			new TemplateEmbeddedInRequest("Vorlage:Normdaten",0),
			new Reference(new Property(143), new ItemSnak(48183)),
			"Normdaten"
			);
	public static final NormdatenTask2Configuration CAWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Categoria:Pàgines utilitzant control d'autoritats amb paràmetres",0),
			new Reference(new Property(143), new ItemSnak(199693)),
			"autoritat","Authority control","Normdaten"
			);
	public static final NormdatenTask2Configuration ITWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Categoria:Voci con template Controllo di autorità con parametri",0),
			new Reference(new Property(143), new ItemSnak(11920)),
			"Controllo di autorità","Authority control","Normdaten"
			);
	public static final NormdatenTask2Configuration PLWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Kategoria:Kontrola autorytatywna z parametrami",0),
			new Reference(new Property(143), new ItemSnak(1551807)),
			"Kontrola autorytatywna","Authority control","Normdaten"
			);	
	public static final NormdatenTask2Configuration JAWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Category:典拠管理をパラメータで指定しているページ",0),
			new Reference(new Property(143), new ItemSnak(177837)),
			"Normdaten","Authority control"
			);
	public static final NormdatenTask2Configuration HUWIKI = new NormdatenTask2Configuration(
			new TemplateEmbeddedInRequest("Sablon:Nemzetközi katalógusok", 0),
			new Reference(new Property(143), new ItemSnak(53464)),
			"Nemzetközi katalógusok","Normdaten","Authority control"
			);
	public static final NormdatenTask2Configuration KOWIKI = new NormdatenTask2Configuration(
			new CategoryMemberRequest("분류:매개 변수를 가진 전거 통제 틀을 사용하는 문서",0),
			new Reference(new Property(143), new ItemSnak(17985)),
			"Authority control","전거 정보","전거 통제","전거 제어","Normdaten"
			);
	public static final NormdatenTask2Configuration ELWIKI = new NormdatenTask2Configuration(
			new TemplateEmbeddedInRequest("Πρότυπο:Authority control", 0),
			new Reference(new Property(143), new ItemSnak(11918)),
			"Authority control"
			);
	public static final NormdatenTask2Configuration BEWIKI = new NormdatenTask2Configuration(
			new TemplateEmbeddedInRequest("Шаблон:Вонкавыя спасылкі", 0),
			new Reference(new Property(143), new ItemSnak(877583)),
			"бібліяінфармацыя","Вонкавыя спасылкі","Нарматыўны кантроль","Authority control","Normdaten"
			);
	public static final NormdatenTask2Configuration CSWIKI = new NormdatenTask2Configuration(
			new TemplateEmbeddedInRequest("Šablona:Autoritní data", 0),
			new Reference(new Property(143), new ItemSnak(191168)),
			"Autoritní data"
			);
	public static final NormdatenTask2Configuration FAWIKI = new NormdatenTask2Configuration(
			new TemplateEmbeddedInRequest("الگو:داده‌های کتابخانه‌ای", 0),
			new Reference(new Property(143), new ItemSnak(48952)),
			"مستند کردن","داده‌های کتابخانه‌ای","Authority control","Normdaten"
			);
	public static final NormdatenTask2Configuration SVWIKI = new NormdatenTask2Configuration(
			new TemplateEmbeddedInRequest("Mall:Auktoritetsdata", 0),
			new Reference(new Property(143), new ItemSnak(169514)),
			"Auktoritetsdata","Katalogreferenser"
			);
	public static final NormdatenTask2Configuration ENWIKISOURCE = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Category:Pages using authority control with parameters", 100, 102),
			new Reference(new Property(143), new ItemSnak(15156406)),
			"Authority control"
			);
	
	public static final NormdatenTask2Configuration CAWIKI_TAXON = new NormdatenTask2Configuration(
			new CategoryMemberRequest("Categoria:Bases de dades taxonòmiques amb paràmetres exportables a Wikidata",0),
			new Reference(new Property(143), new ItemSnak(199693)),
			"Bases de dades taxonòmiques","BDT"
			);
	
	static {
		CAWIKI_TAXON.setLowerCaseMode(true);
		CAWIKI.setKeepEmpty(true);
		FRWIKI.setSummary("Les valeurs des paramètres de {{Autorité}} ont été transférées vers Wikidata");
		HUWIKI.setSummary("nemzetközi katalógusok a Wikidatából");
		FAWIKI.setSummary("انتقال داده‌های کتابخانه‌ای به ویکی‌داده");
	}
	
	
	public NormdatenTask2Configuration(MediaWikiRequest<List<Article>> r, Reference ref, String...t) {
		this.request = r;
		this.template = t;
		this.reference = ref;
	}
	
	private MediaWikiRequest<List<Article>> request;
	private String[] template;
	private Reference reference;
	private boolean keepEmpty = false;
	private boolean lowerCaseMode = false;
	private String summary = null;
	
	
	public MediaWikiRequest<List<Article>> getRequest() {
		return request;
	}

	public String getTemplate() {
		return template[0];
	}
	
	public String[] getTemplates(){
		return template;
	}

	public void setRequest(MediaWikiRequest<List<Article>> request) {
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

	public boolean isKeepEmpty() {
		return keepEmpty;
	}

	public void setKeepEmpty(boolean keepEmpty) {
		this.keepEmpty = keepEmpty;
	}

	public boolean isLowerCaseMode() {
		return lowerCaseMode;
	}

	public void setLowerCaseMode(boolean lowerCaseMode) {
		this.lowerCaseMode = lowerCaseMode;
	}

	
	public String getSummary() {
		return summary == null ? getTemplate()+" moved to Wikidata" : summary;
	}
	

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
