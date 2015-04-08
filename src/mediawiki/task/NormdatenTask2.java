package mediawiki.task;

import static main.GNDLoad.addClaim;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONObject;

import main.GNDLoad;
import mediawiki.ArticleDenier;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaTask;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.CategoryMemberRequest;
import mediawiki.request.GetTemplateValuesRequest;
import mediawiki.request.TemplateEmbeddedInRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;

public class NormdatenTask2 extends WikipediaWikidataTask {

	private Article startAt;
	
	private Reference ref;
	private ArticleDenier denier;
	
	public NormdatenTask2(WikimediaConnection wikidata, WikimediaConnection wikipedia){
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
			
			InputStream in = NormdatenTask2.class.getResourceAsStream("authoritycontrol.json");
			StringBuffer b = new StringBuffer();
			while(in.available() > 0){
				byte[] buffer = new byte[1024];
				in.read(buffer);
				b.append(buffer);
			}
			in.close();
			JSONObject ac = new JSONObject(b.toString());
			
			
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
					
					HashMap<String,String> t = getWikipediaConnection().request(new GetTemplateValuesRequest(a.getTitle(), "Authority control"));
					
					for(Entry<String, String> e : t.entrySet()){
						
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
