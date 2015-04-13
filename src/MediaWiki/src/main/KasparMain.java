package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import mediawiki.ArticleDenier;
import mediawiki.WikimediaConnection;
import mediawiki.WikimediaException;
import mediawiki.WikimediaRequest;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.TranslatedContent;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.request.CategoryMemberRequest;
import mediawiki.request.ContentRequest;
import mediawiki.request.EditRequest;
import mediawiki.request.LoginRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.GetDescriptionRequest;
import mediawiki.request.wikibase.GetLabelRequest;
import mediawiki.request.wikibase.HasClaimRequest;
import mediawiki.request.wikibase.SetDescriptionRequest;
import mediawiki.request.wikibase.SetReferenceRequest;

import util.Util;

public class KasparMain {

	/**
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws Exception {
		String username = Config.USERNAME;
		String password = Config.PASSWORD;
		
		WikimediaConnection wikidata = new WikimediaConnection("www","wikidata.org");
		wikidata.request(new LoginRequest(username, password));
		wikidata.setBot(true);
		
				
		WikimediaConnection wikipedia = new WikimediaConnection("de","wikipedia.org");
		// wikipedia.request(new LoginRequest(username, password));
		// wikipedia.setBot(true);

		ArrayList<Task> tasks = new ArrayList<>();
		
		
		{
			Task t1 = new Task();
			t1.attributes.add(new Claim(166,2051240));
			t1.attributes.add(new Claim(31,5));
			
			t1.attributes.add(new Claim(106,39631));
			
			
			t1.kats = new String[]{
					"Kategorie:Träger der Paracelsus-Medaille"
			};
			
			t1.denier.register("Paracelsus-Medaille");
			
			t1.source = new Claim(143, 48183); // 48183
			
			
			tasks.add(t1);
		}

		
		
		
		
		
		
		// INPUT !!!!!!!!!!!
		
		System.out.println("Confirmation sequence?");
		if(Util.in().equals("yes")){
			for(Task t : tasks){
				if( ! t.isComplete()){
					System.err.println("Nicht alle Felder ausgefüllt!");
					System.exit(1);
				}
				String[] kats = t.kats;
				ArticleDenier a = t.denier;
				ArrayList<Claim> attributes = t.attributes;
				
				System.out.println("Please confirm with yes!");
				System.out.println("Categories:");
				int sum = 0;
				for(String kat : kats){
					int count = ((List<?>)wikipedia.request(new CategoryMemberRequest(kat, 0))).size();
					System.out.println(" · "+kat+" ("+count+" members)");
					sum += count;
				}
				System.out.println(a.getRulesCount()+" entities will be skipped.");
				System.out.println((sum-a.getRulesCount())+" entities will be affected.");
				if(! Util.in().equals("yes")){
					System.out.println("No confirmation. Exit.");
					System.exit(0);
				}
				System.out.println("Property:");
				for(Claim attribute : attributes){
					String pname = (String) wikidata.request(new GetLabelRequest("de", attribute.getProperty().toString()));
					String qname = (String) wikidata.request(new GetLabelRequest("de", "Q"+((ItemSnak)attribute.getSnak()).getID()));
					System.out.println(attribute.getProperty()+"=Q"+((ItemSnak)attribute.getSnak()).getID());
					
					System.out.println(pname+"="+qname);
					
					
					
			/*		List<Integer> s = new WikidataQuery("CLAIM[279:"+((ItemEntity)attribute.getEntity()).getID()+"]").request();
				
					
					if(s.size() > 1){
						System.out.println(s.size()+" subclasses. Display?");
						if(Util.in().equals("yes")){
							System.out.println("Subclasses:");
							for(Integer i : s){
								if(i == ((ItemEntity)attribute.getEntity()).getID())
									continue;
								System.out.println(" · Q"+i+" "+(String) wikidata.request(new GetLabelRequest("de", "Q"+i)));
							}
						}
					}else{
						System.out.println("no subclasses");
					} */
					
					
					
					
					if(! Util.in().equals("yes")){
						System.out.println("No confirmation. Exit.");
						System.exit(0);
					}
				} 
				
				
				System.out.println("Source:");
				String pname = (String) wikidata.request(new GetLabelRequest("de", t.source.getProperty().toString()));
				String qname = (String) wikidata.request(new GetLabelRequest("de", "Q"+((ItemSnak)t.source.getSnak()).getID()));
				System.out.println(t.source.getProperty()+"=Q"+((ItemSnak)t.source.getSnak()).getID());
				System.out.println(pname+"="+qname);
				if(! Util.in().equals("yes")){
					System.out.println("No confirmation. Exit.");
					System.exit(0);
				}
				
			
			}
		}
		
		
		

		System.out.println("Final confirm:");
		if(! Util.in().equals("yes")){
			System.out.println("No confirmation. Exit.");
			System.exit(0);
		}
		
		
		for(Task t : tasks){
			changeWikidataInCategory(wikidata,wikipedia,t);
		}
		
		String text = (String) wikidata.request(new ContentRequest("User:KasparBot/Kaspar/Completed"));
		for(Task t : tasks){
			for(String kat : t.kats){
				text += "\n* [[:"+wikipedia.getLanguage()+":"+kat+"]]";
				for(Claim attribute : t.attributes){
					text += "\n** {{Statement||"+attribute.getProperty().toString()+"|"+((ItemSnak)attribute.getSnak()).getID()+"}}";
				}
			}
		}
		wikidata.request(new EditRequest("User:KasparBot/Kaspar/Completed", text, "update"));
		
		
		
		System.out.println("Statistik für Wikidata:");
		for(Entry<Class<? extends WikimediaRequest<?>>, Integer> e : wikidata.getStatistic().entrySet()){
			System.out.println(e.getValue()+"\t"+e.getKey().getCanonicalName());
		}
		System.out.println("Statistik für Wikipedia:");
		for(Entry<Class<? extends WikimediaRequest<?>>, Integer> e : wikipedia.getStatistic().entrySet()){
			System.out.println(e.getValue()+"\t"+e.getKey().getCanonicalName());
		}
		wikidata.close();
		wikipedia.close();
	}
	
	public static void changeWikidataInCategory(WikimediaConnection wikidata,
			WikimediaConnection wikipedia, Task t) throws Exception {
		for(String kat : t.kats){
			changeWikidataInCategory(wikidata,wikipedia,kat,t.attributes,t.source,t.denier,t.description);
		}
	}
	
	/**
	 * Add claims to existing Wikidata items based on defined Wikipedia category classification
	 * @param wikidata WikimediaConnection to the Wikidata API
	 * @param wikipedia WikimediaConnection to the Wikipedia API (e.g. de.wikipedia)
	 * @param kat the Wikipedia category which should be scanned (e.g. Kategorie:Jordanier)
	 * @param prop the Wikidata property which should be added (e.g. P27)
	 * @param propvalue a Wikidata item (without Q at the beginning) which should be the value (e.g. 810)
	 * @param sourceprop the Wikidata property which should be added as references (e.g. P143)
	 * @param sourceentity a Wikidata item (without Q at the beginning) which should be the value of the reference (e.g. 48183)
	 * @throws Exception 
	 */
	public static void changeWikidataInCategory(
			WikimediaConnection wikidata,
			WikimediaConnection wikipedia,
			String kat,
			ArrayList<Claim> attributes,
			Claim source,
			ArticleDenier deny,
			TranslatedContent<String> description
			) throws Exception{
		System.out.println("Looking up [["+wikipedia.getLanguage()+":"+kat+"]]...");
		List<Article> r = ((List<Article>) wikipedia.request(new CategoryMemberRequest(kat,0)));
		System.out.println(r.size()+" articles in [["+wikipedia.getLanguage()+":"+kat+"]]");
		
		int repition = 0;
		
		for(int i = 0; i < r.size(); i++){
			Article a = r.get(i);
				try{
					if(deny.isDeniable(a))
						continue;
					
					System.out.print(i+".\t");
					String base;
					try{
						base = (String) wikipedia.request(new WikiBaseItemRequest(a));
					}catch(WikimediaException e){
						System.out.println(a.getTitle()+" without Wikidata item");
						continue;
					}catch(NullPointerException e){
						System.out.println(a.getTitle()+" without Wikidata item");
						continue;
					}
					for(Claim attribute : attributes){
						try{
							Boolean result = (Boolean) wikidata.request(new HasClaimRequest(base,attribute.getProperty()));
							if(result){
							/*	try{
									System.out.println(a.getTitle()+" ("+base+") has already a claim for "+attribute.getProperty());
									
									GetSpecificClaimRequest s = new GetSpecificClaimRequest(base, attribute);
									ArrayList<Claim> c = (ArrayList<Claim>) wikidata.request(s);
									if(c.isEmpty()){
										System.out.println("no claim with this value");
										continue;
									}
									if(c.get(0).hasReferences()){
										System.out.println("claim with this value has already a reference");
										continue;
									}
									WikimediaRequest req2 = new SetReferenceRequest(c.get(0).getId(), source);
									req2.setProperty("summary", "processed by [[User:KasparBot/Kaspar|Kaspar 1.0]] based on [["+wikipedia.getLanguage()+":"+kat+"]]");
									wikidata.request(req2);
									System.out.println("reference added");
									continue;
								}catch(IndexOutOfBoundsException e){
									System.out.println("reference check failed");
									continue;
								} */
								System.out.println(a.getTitle()+" ("+base+") has already this claim.");
								continue;
								
							}
						}catch(WikimediaException e){
							System.out.println(a.getTitle()+" without Wikidata item");
							continue;
						}
						try{
							addWikidataProperty(wikidata, base, attribute, source, "processed by [[User:KasparBot/Kaspar|Kaspar 1.0]] based on [["+wikipedia.getLanguage()+":"+kat+"]]");
							System.out.println(a.getTitle()+" ("+base+") edited.");
						}catch(WikimediaException e){
							if(e.getMessage().indexOf("Please try again in a few minutes.") > 0){
								System.out.println("Limit exceeded. Begin waiting vor 1 minute...");
									try {
										Thread.currentThread();
										Thread.sleep(60000);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
								System.out.println("Continuing...");
								try{
									addWikidataProperty(wikidata, base, attribute, source, "processed by [[User:KasparBot/Kaspar|Kaspar 1.0]] based on [["+wikipedia.getLanguage()+":"+kat+"]]");
									System.out.println(a.getTitle()+" ("+base+") edited.");
								}catch(WikimediaException e2){
									continue;
								}
							}else{
								throw e;
							}
						}
					}
					for(Entry<String, String> e : description){
						try{
							GetDescriptionRequest g = new GetDescriptionRequest(base, e.getKey());
							String desc = (String) wikidata.request(g);
							if(desc != null){
								System.out.println("Has already a description for "+e.getKey()+": "+desc);
								continue;
							}
							SetDescriptionRequest s = new SetDescriptionRequest(base, e.getKey(), e.getValue(), "processed by [[User:KasparBot/Kaspar|Kaspar 1.0]] based on [["+wikipedia.getLanguage()+":"+kat+"]]");
							wikidata.request(s);
							System.out.println("Description created");
						}catch(Exception e2){}
					}
					
					
					repition = 0;
				}catch(Exception e){
					if(repition < 3){
						System.err.println("Unknown exception: "+e.getClass().getCanonicalName()+" "+e.getMessage());
						e.printStackTrace();
						System.exit(1);
						
				/*		try {
							Thread.currentThread().sleep(60000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						wikidata.relogin();
						i--;
						repition++; */ 
					}
				}
			} 
		
	}
	
	public static Statement addWikidataProperty(WikimediaConnection wikidata, String entity, Claim attr, Reference source, String message) throws Exception{
		CreateClaimRequest req = new CreateClaimRequest(entity, attr);
		req.setProperty("summary", message);
		Statement claim = (Statement) wikidata.request(req);
		if(source != null){
			WikimediaRequest req2 = new SetReferenceRequest(claim, source);
			req2.setProperty("summary", message);
			wikidata.request(req2);
		}
		return claim;
	}
	
	public static void addWikidataProperty(WikimediaConnection wikidata, String entity, Claim attr, Claim source, String message) throws Exception{
		addWikidataProperty(wikidata, entity, attr, new Reference(source), message);
	}
}
