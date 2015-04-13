package mediawiki.task;


import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import mediawiki.request.GetTemplatesValuesRequest;
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
			
			InputStream in = NormdatenTask2.class.getResourceAsStream("authoritycontrol.json");
			StringBuffer b = new StringBuffer();
			while(in.available() > 0){
				byte[] buffer = new byte[1024];
				in.read(buffer);
				b.append(new String(buffer));
			}
			in.close();
			final JSONObject ac = new JSONObject(b.toString());
			
			
			TemplateEmbeddedInRequest p = new TemplateEmbeddedInRequest("Template:Authority control",0);
			p.setProperty("eidir", "descending");
		//	p.setLimit(1000); for testing
			articles = (List<Article>) getWikipediaConnection().request(p);
			System.out.println("Alles geladen");
			
			
			
			for(Article a : articles){
				System.out.println(a.getTitle());
				try{
					
					String base = (String) getWikipediaConnection().request(new WikiBaseItemRequest(a));
					if(base == null){
						System.err.println(a.getTitle()+"\tno wikidata item");
						continue;
					}
					
					
					List<Map<String,String>> t2 = getWikipediaConnection().request(new GetTemplatesValuesRequest(a.getTitle(), "Authority control"));
					if(t2.size() != 1){
						System.err.println(a.getTitle()+"\tunknown alias embedded or more than one template embedded");
						continue;
					}
					Map<String, String> t = t2.get(0);
					
					if(t.size() == 0){
						System.err.println(a.getTitle()+"\talready moved to wikidata");
						continue;
					}
						
					boolean removable = true;
					for(Entry<String, String> e : t.entrySet()){
						if(e.getKey().equalsIgnoreCase("TYP"))
							continue;
						if(! ac.has(e.getKey())){
							System.err.println(a.getTitle()+"\tunknown template property: "+e.getKey());
							removable = false;
						}else{
							if(e.getValue().trim().length() == 0)
								continue;
							String value = e.getKey().equals("LCCN") && ! e.getValue().matches(ac.getJSONObject(e.getKey()).getString("pattern")) ? WikimediaUtil.formatLCCN(e.getValue()) : e.getValue();
							if(value == null || ! value.matches(ac.getJSONObject(e.getKey()).getString("pattern"))){
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
										System.out.println(a.getTitle()+"\tadded claim for "+e.getKey());
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
					
					if(getWikipediaConnection().request(new GetTemplateValuesRequest(a, "bots")) != null){
						System.err.println(a.getTitle()+"\tbot-template found");
						continue;
					}
					
					if(removable){
						System.out.println(a.getTitle()+"\ttemplate can be removed");
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
