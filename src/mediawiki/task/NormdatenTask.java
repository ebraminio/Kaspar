package mediawiki.task;

import static main.GNDLoad.addClaim;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import main.GNDLoad;
import mediawiki.ArticleDenier;
import mediawiki.WikimediaConnection;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.GetTemplateValuesRequest;
import mediawiki.request.TemplateEmbeddedInRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;

public class NormdatenTask extends WikipediaWikidataTask {

	private Article startAt;
	
	private Reference ref;
	private ArticleDenier denier;
	
	public NormdatenTask(WikimediaConnection wikidata, WikimediaConnection wikipedia){
		super(wikidata, wikipedia);
		
		ref = new Reference(new Property(143), new ItemSnak(328));
	}

	@Override
	public void run() {
		List<Article> articles;
		try {
			TemplateEmbeddedInRequest p = new TemplateEmbeddedInRequest("Template:Authority control",0);
			p.setProperty("eidir", "descending");
			articles = (List<Article>) getWikipediaConnection().request(p);
			setTogo(articles.size());
			recordETA();
			System.out.println("Alles geladen");
			
			boolean f = (startAt == null ? false : true);
			for(Article a : articles){
				if(isStopped())
					return;
				if(a.isIdentical(startAt))
					f = false;
				if(f){
					increaseDone();
					continue;
				}
				if(denier != null && denier.isDeniable(a)){
					increaseDone();
					continue;
				}
				try{
					String base = (String) getWikipediaConnection().request(new WikiBaseItemRequest(a));
					if(base == null){
						increaseDone();
						continue;
					}
					
					Statement instance = getConnection().request(new GetSpecificStatementRequest(base, new Property(31))).get(0);
					int instancevalue = ((ItemSnak)instance.getClaim().getSnak()).getValue();
					
					int[] skip = new int[]{4167410, 4167836, 13406463, 15138389};
					if(Arrays.asList(skip).contains(instancevalue)) {
						System.err.println("Q"+instancevalue+ " isn't supported");
						continue;
					}
					
					Map<String,String> t = getWikipediaConnection().request(new GetTemplateValuesRequest(a.getTitle(), "Authority control"));
					
					if(t.get("GND") != null && ! t.get("GND").equals("") && t.get("GND").matches("(1|10)\\d{7}[0-9X]|[47]\\d{6}-\\d|[1-9]\\d{0,7}-[0-9X]|3\\d{7}[0-9X]")){
						GNDLoad.addClaim(getConnection(), base, new Claim(227, new StringSnak(t.get("GND"))), ref, "based on enwiki Authority control");
					}
					if(t.get("NDL") != null && ! t.get("NDL").equals("") && t.get("NDL").matches("0?\\d{8}")){
						addClaim(getConnection(), base, new Claim(new Property(349),new StringSnak(t.get("NDL"))), ref, "based enwiki Authority control");
					}
					if(t.get("VIAF") != null && ! t.get("VIAF").equals("") && t.get("VIAF").matches("([1-9]\\d{1,8}|)")){
						addClaim(getConnection(), base, new Claim(new Property(214),new StringSnak(t.get("VIAF"))), ref, "based on enwiki Authority control");
					}
					if(t.get("ISNI") != null && ! t.get("ISNI").equals("") && t.get("ISNI").matches("\\d\\d\\d\\d \\d\\d\\d\\d \\d\\d\\d\\d \\d\\d\\d[\\dX]")){
						addClaim(getConnection(), base, new Claim(new Property(213),new StringSnak(t.get("ISNI"))), ref, "based on enwiki Authority control");
					}
					if(t.get("SELIBR") != null && ! t.get("SELIBR").equals("") && t.get("SELIBR").matches("[1-9]\\d{0,8}")){
						addClaim(getConnection(), base, new Claim(new Property(906),new StringSnak(t.get("SELIBR"))), ref, "based on enwiki Authority control");
					}
					if(t.get("SUDOC") != null && ! t.get("SUDOC").equals("") && t.get("SUDOC").matches("\\d{8}[\\dX]")){
						addClaim(getConnection(), base, new Claim(new Property(269),new StringSnak(t.get("SUDOC"))), ref, "based on enwiki Authority control");
					}
					if(t.get("BNF") != null && ! t.get("BNF").equals("") && t.get("BNF").matches("\\d{8}[0-9bcdfghjkmnpqrstvwxz]")){
						addClaim(getConnection(), base, new Claim(new Property(268),new StringSnak(t.get("BNF"))), ref, "based on enwiki Authority control");
					}
					if(t.get("BPN") != null && ! t.get("BPN").equals("") && t.get("BPN").matches("\\d{8}")){
						addClaim(getConnection(), base, new Claim(new Property(651),new StringSnak(t.get("BPN"))), ref, "based on enwiki Authority control");
					}
					if(t.get("RID") != null && ! t.get("RID").equals("") && t.get("RID").matches("[A-Z]-\\d{4}-(19|20)\\d\\d")){
						addClaim(getConnection(), base, new Claim(new Property(1053),new StringSnak(t.get("RID"))), ref, "based on enwiki Authority control");
					}
					if(t.get("BIBSYS") != null && ! t.get("BIBSYS").equals("") && t.get("BIBSYS").matches("x\\d{8}")){
						addClaim(getConnection(), base, new Claim(new Property(1015),new StringSnak(t.get("BIBSYS"))), ref, "based on enwiki Authority control");
					}
					if(t.get("ULAN") != null && ! t.get("ULAN").equals("") && t.get("ULAN").matches("500\\d{6}")){
						addClaim(getConnection(), base, new Claim(new Property(245),new StringSnak(t.get("ULAN"))), ref, "based on enwiki Authority control");
					}
					if(t.get("MBA") != null && ! t.get("MBA").equals("") && t.get("MBA").matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")){
						addClaim(getConnection(), base, new Claim(new Property(434),new StringSnak(t.get("MBA"))), ref, "based on enwiki Authority control");
					}
					if(t.get("NLA") != null && ! t.get("NLA").equals("") && t.get("NLA").matches("[1-9][0-9]{0,11}")){
						addClaim(getConnection(), base, new Claim(new Property(409),new StringSnak(t.get("NLA"))), ref, "based on enwiki Authority control");
					}
					if(t.get("RKDartists") != null && ! t.get("RKDartists").equals("") && t.get("RKDartists").matches("[1-9]\\d{0,5}")){
						addClaim(getConnection(), base, new Claim(new Property(650),new StringSnak(t.get("RKDartists"))), ref, "based on enwiki Authority control");
					}
					
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}
				increaseDone();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setStartAt(Article startAt) {
		this.startAt = startAt;
	}
	
	public Article getStartAt() {
		return startAt;
	}

	public Reference getReference() {
		return ref;
	}

	public void setReference(Reference ref) {
		this.ref = ref;
	}

	public ArticleDenier getDenier() {
		return denier;
	}

	public void setDenier(ArticleDenier denier) {
		this.denier = denier;
	}
}
