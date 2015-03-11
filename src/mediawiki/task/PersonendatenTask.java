package mediawiki.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mediawiki.ArticleDenier;
import mediawiki.WikidataQuery;
import mediawiki.WikimediaConnection;
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
import mediawiki.request.wikibase.SetDescriptionRequest;

import static main.GNDLoad.addClaim;

public class PersonendatenTask extends WikipediaWikidataTask {

	private String kat;
	private Claim[] claims;
	
	private Article startAt;
	private Connection connect;
	
	private Reference ref;
	private ArticleDenier denier;
	
	public PersonendatenTask(WikimediaConnection con, WikimediaConnection wikipedia, Connection connect, String kat, Claim...claims) {
		super(con,wikipedia);
		this.kat = kat;
		this.claims = claims;
		this.connect = connect;
		
		ref = new Reference(new Property(143), new ItemSnak(48183));
	}

	@Override
	public void run() {
		
		
		try {
			
			PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO gnddata (gnd, wikibase) VALUES (?,?)");
			SimpleDateFormat log = new SimpleDateFormat("HH:mm:ss");
			
			List<Article> articles = (List<Article>) getWikipediaConnection().request(new CategoryMemberRequest(kat,0));
			
			boolean f = (startAt == null ? false : true);
			
			for(Article a : articles){
				if(a.isIdentical(startAt))
					f = false;
				if(f)
					continue;
				if(denier != null && denier.isDeniable(a))
					continue;
				try{
					String base = (String) getWikipediaConnection().request(new WikiBaseItemRequest(a));
					if(base == null)
						continue;
					HashMap<String,String> t = getWikipediaConnection().request(new GetTemplateValuesRequest(a.getTitle(), "Personendaten"));
					if(t != null){
						String desc = t.get("KURZBESCHREIBUNG");
						if(desc != null){
							desc = desc.replaceAll("\\[\\[([\\p{IsAlphabetic}\\s]+\\|)?([\\p{IsAlphabetic}\\s]+)\\]\\]", "$2");
							if(desc.indexOf('[') != -1 || desc.indexOf('{') != -1)
								continue;
							String title = (String)getConnection().request(new GetDescriptionRequest(base, "de"));
							if(title == null){
								getConnection().request(new SetDescriptionRequest(base, "de", desc, "based on dewiki person data"));
								System.out.println(Thread.currentThread().getName()+"\t["+log.format(new Date())+"]\t"+base+" has now a description: "+desc);
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
							if(addClaim(getConnection(), base, new Claim(new Property(227),new StringSnak(n.get("GND"))), ref, "processed by Kaspar using authority data on dewiki") != null){
								preparedStatement.setString(1, n.get("GND"));
								preparedStatement.setString(2, base);
								preparedStatement.execute();
							}
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
			}
			
			
		} catch (Exception e) {
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
