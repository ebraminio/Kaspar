package mediawiki.task;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaUtil;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.ContentRequest;
import mediawiki.request.EditRequest;
import mediawiki.request.GetTemplateValuesRequest;
import mediawiki.request.TemplateEmbeddedInRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.SetReferenceRequest;

import org.json.JSONObject;

public class NormdatenTask2 extends WikipediaWikidataTask {

	
	private Reference ref;
	
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
			System.out.println("Alles geladen");
			
			InputStream in = NormdatenTask2.class.getResourceAsStream("authoritycontrol.json");
			StringBuffer b = new StringBuffer();
			while(in.available() > 0){
				byte[] buffer = new byte[1024];
				in.read(buffer);
				b.append(buffer);
			}
			in.close();
			final JSONObject ac = new JSONObject(b.toString());
			
			for(Article a : articles){
				try{
					if(getWikipediaConnection().request(new GetTemplateValuesRequest(a, "bots")) != null){
						continue;
					}
					
					
					String base = (String) getWikipediaConnection().request(new WikiBaseItemRequest(a));
					if(base == null){
						continue;
					}
					
					
					HashMap<String,String> t = getWikipediaConnection().request(new GetTemplateValuesRequest(a.getTitle(), "Authority control"));
					
					boolean removable = true;
					
					for(Entry<String, String> e : t.entrySet()){
						if(ac.getString(e.getKey()) == null){
							System.err.println(a.getTitle()+"\tunknown template property: "+e.getKey());
							removable = false;
						}else{
							String value = e.getKey().equals("LCCN") ? WikimediaUtil.formatLCCN(e.getValue()) : e.getValue();
							if(! value.matches(ac.getJSONObject(e.getKey()).getString("pattern"))){
								System.err.println(a.getTitle()+"\tmalformed value for "+e.getKey());
								removable = false;
							}else{
								List<Statement> l = getConnection().request(new GetSpecificStatementRequest(base, new Property(ac.getJSONObject(e.getKey()).getInt("property"))));
								if(l.size() == 0){
									Statement s = getConnection().request(new CreateClaimRequest(base, new Claim(ac.getJSONObject(e.getKey()).getInt("property"), new StringSnak(value))));
									if(s == null){
										System.err.println(a.getTitle()+"\tunable to add claim for "+e.getKey());
										removable = false;
									}else{
										getConnection().request(new SetReferenceRequest(s, getReference()));
									}
								}else{
									boolean flag2 = false;
									for(Statement s : l){
										if(s.getClaim().getSnak().getValue().equals(value)){
											flag2 = true;
										}
									}
									if(!flag2)
										System.err.println(a.getTitle()+"\tdifferent value on wikidata for "+e.getKey());
									removable = flag2 ? removable : false;
								}
							}
						}
					}
					
					if(removable){
						String old = getWikipediaConnection().request(new ContentRequest(a));
						String nw  = old.replaceAll("\\{\\{Authority\\ control[\\|A-Za-z0-9\\=\\ \\/\\-]+\\}\\}", "{{Authority control}}");
						getWikipediaConnection().request(new EditRequest(a, nw, "authority control moved to wikidata"));
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

	public Reference getReference() {
		return ref;
	}

	public void setReference(Reference ref) {
		this.ref = ref;
	}

}
