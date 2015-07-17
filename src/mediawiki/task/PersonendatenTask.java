package mediawiki.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mediawiki.ArticleDenier;
import mediawiki.MediaWikiConnection;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.WikibaseDate;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.CategoryMemberRequest;
import mediawiki.request.GetTemplateValuesRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.GetDescriptionRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.SetDescriptionRequest;

import static main.GNDLoad.addClaim;

public class PersonendatenTask extends WikipediaWikidataTask {

	private String kat;
	private Claim[] claims;
	
	private Article startAt;
	
	private Reference ref;
	private ArticleDenier denier;
	
	public PersonendatenTask(MediaWikiConnection con, MediaWikiConnection wikipedia, String kat, Claim...claims) {
		super(con,wikipedia);
		this.kat = kat;
		this.claims = claims;
		
		ref = new Reference(new Property(143), new ItemSnak(48183));
	}

	@Override
	public void run() {
		
		
		try {
			
			SimpleDateFormat log = new SimpleDateFormat("HH:mm:ss");
			
			CategoryMemberRequest cmr = new CategoryMemberRequest(kat,0);
			cmr.setProperty("cmdir", "newer");
			List<Article> articles = getWikipediaConnection().request(cmr);
			setTogo(articles.size());
			recordETA();
			
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
					if(getConnection().request(new GetSpecificStatementRequest(base, new Claim(31, new ItemSnak(5)))).size() == 0){
						System.err.println(base+" isn't a human!");
						increaseDone();
						continue;
					}
					Map<String,String> t = getWikipediaConnection().request(new GetTemplateValuesRequest(a.getTitle(), "Personendaten"));
					if(t != null){
						String desc = t.get("KURZBESCHREIBUNG");
						if(desc != null){
							desc = desc.replaceAll("\\[\\[([\\p{IsAlphabetic}\\s]+\\|)?([\\p{IsAlphabetic}\\s]+)\\]\\]", "$2");
							if(desc.indexOf('[') == -1 && desc.indexOf('{') == -1){
								String title = (String)getConnection().request(new GetDescriptionRequest(base, "de"));
								if(title == null){
									getConnection().request(new SetDescriptionRequest(base, "de", desc, "based on dewiki person data"));
									System.out.println(Thread.currentThread().getName()+"\t["+log.format(new Date())+"]\t"+base+" has now a description: "+desc);
								}
							}
						}
						if(t.get("GEBURTSDATUM") != null){
							WikibaseDate wbd = WikibaseDate.parseWikipediaDate(t.get("GEBURTSDATUM"));
							if(wbd != null){
								addClaim(getConnection(), base, new Claim(new Property(569),new DateSnak(wbd)), ref, "processed by Kaspar using person data on dewiki");
							}
						}
						if(t.get("STERBEDATUM") != null){
							WikibaseDate wbd = WikibaseDate.parseWikipediaDate(t.get("STERBEDATUM"));
							if(wbd != null){
								addClaim(getConnection(), base, new Claim(new Property(570),new DateSnak(wbd)), ref, "processed by Kaspar using person data on dewiki");
							}
						}
					}
					HashMap<String,String> n = (HashMap<String, String>) getWikipediaConnection().request(new GetTemplateValuesRequest(a.getTitle(), "Normdaten"));
					if(n != null){
						if(n.get("GND") != null && ! n.get("GND").equals("")){
							addClaim(getConnection(), base, new Claim(new Property(227),new StringSnak(n.get("GND"))), ref, "processed by Kaspar using authority data on dewiki");
						}
					//	if(n.get("LCCN") != null && ! n.get("LCCN").equals("")){
					//		addClaim(getConnection(), base, new Claim(new Property(244),new StringSnak(n.get("LCCN"))), ref, "processed by Kaspar using authority data on dewiki");
					//	}
						if(n.get("NDL") != null && ! n.get("NDL").equals("")){
							addClaim(getConnection(), base, new Claim(new Property(349),new StringSnak(n.get("NDL"))), ref, "processed by Kaspar using authority data on dewiki");
						}
						if(n.get("VIAF") != null && ! n.get("VIAF").equals("")){
							addClaim(getConnection(), base, new Claim(new Property(214),new StringSnak(n.get("VIAF"))), ref, "processed by Kaspar using authority data on dewiki");
						}
					}
					HashMap<String,String> imdb = (HashMap<String, String>) getWikipediaConnection().request(new GetTemplateValuesRequest(a.getTitle(), "IMDb Name"));
					if(imdb != null){
						if(imdb.get("1") != null && ! imdb.get("1").equals("")){
							addClaim(getConnection(), base, new Claim(new Property(345),new StringSnak("nm"+imdb.get("1"))), ref, "processed by Kaspar using imdb data on dewiki");
						}
					}
					
					for(Claim c : claims){
						addClaim(getConnection(), base, c, ref, "processed by Kaspar using "+kat);
					}
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}
				increaseDone();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		fireCompletedEvent();
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
