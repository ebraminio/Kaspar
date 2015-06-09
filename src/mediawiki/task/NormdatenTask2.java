package mediawiki.task;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaUtil;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.info.wikibase.snaks.URLSnak;
import mediawiki.request.CategoryMemberRequest;
import mediawiki.request.ContentRequest;
import mediawiki.request.EditRequest;
import mediawiki.request.GetTemplatesValuesRequest;
import mediawiki.request.TemplateEmbeddedInRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.SetReferenceRequest;

import org.json.JSONObject;

import util.GetRequest;

import datasets.in.GND;
import datasets.in.VIAF;

public class NormdatenTask2 extends WikipediaWikidataTask {

	
	private NormdatenTask2Configuration config;
	
	public NormdatenTask2(WikimediaConnection wikidata, WikimediaConnection wikipedia, NormdatenTask2Configuration c){
		super(wikidata, wikipedia);
		
		config = c;
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
			
			if(config.getRequest() instanceof CategoryMemberRequest){
				config.getRequest().setProperty("cmdir", "newer");
			}else if(config.getRequest() instanceof TemplateEmbeddedInRequest){
				config.getRequest().setProperty("eidir", "descending");
			}
			
			articles = getWikipediaConnection().request(config.getRequest()); 
			System.out.println("Alles geladen");
			
			
			
			for(Article a : articles){
				
				System.out.println("* [["+a.getTitle()+"]] "+new Date().toGMTString());
				try{
					
					String base = (String) getWikipediaConnection().request(new WikiBaseItemRequest(a));
					if(base == null){
						System.out.println("** no wikidata item");
						continue;
					}
					List<Map<String,String>> t2 = null;
					for(String template : config.getTemplates()){
						GetTemplatesValuesRequest gtvr = new GetTemplatesValuesRequest(a.getTitle(), template);
						gtvr.setUppercaseMode(true);
						t2 = getWikipediaConnection().request(gtvr);
						if(! t2.isEmpty())
							break;
					}
					if(t2 == null || t2.size() == 0){
						System.out.println("** unknown alias embedded");
						continue;
					}
					
					if(t2.size() > 1){
						System.out.println("** more than one template embedded");
						continue;
					}
					Map<String, String> t = t2.get(0);
					
					if(t.size() == 0){
						System.out.println("** already moved to wikidata");
						continue;
					}
						
					boolean removable = true;
					HashMap<String,String> newParameters = new HashMap<>();
					for(Entry<String, String> e : t.entrySet()){
						if(		e.getKey().equalsIgnoreCase("TYP") ||
								e.getKey().equalsIgnoreCase("TYPE") ||
								e.getKey().equalsIgnoreCase("GNDCheck") ||
								e.getKey().equalsIgnoreCase("GNDfehlt") ||
								e.getKey().equalsIgnoreCase("GNDName") ||
								e.getKey().equalsIgnoreCase("TIMESTAMP") ||
								e.getKey().equalsIgnoreCase("TSURL") ||
								e.getKey().equalsIgnoreCase("NOTES") ||
								e.getKey().equalsIgnoreCase("REMARK") ||
								e.getKey().equalsIgnoreCase("BARE") ||
								e.getKey().equalsIgnoreCase("PREFIX") ||
								e.getKey().equalsIgnoreCase("TESTCASE")
								)
							continue;
						if(e.getKey().equalsIgnoreCase("1") && e.getValue().trim().length() == 0)
							continue;
						if(! ac.has(e.getKey()) && (e.getValue().trim().length() > 0 || (config.isKeepEmpty() && e.getValue().trim().length() == 0) ) ){
							System.out.println("** unknown template property: "+e.getKey());
							newParameters.put(e.getKey(),e.getValue());
							removable = false;
						}else{
							if(e.getValue().trim().length() == 0){
								if(config.isKeepEmpty()){
									newParameters.put(e.getKey(),"");
									removable = false;
									System.out.println("** keep-empty-mode. empty template property: "+e.getKey());
								}
								continue;
							}
							String value = e.getKey().equals("LCCN") && ! e.getValue().matches(ac.getJSONObject(e.getKey()).getString("pattern")) ? WikimediaUtil.formatLCCN(e.getValue()) : e.getValue();
							if(e.getKey().equalsIgnoreCase("ISNI")){
								value = value.replaceAll("(\\d{4})(\\d{4})(\\d{4})(\\d{3}[\\dX])", "$1 $2 $3 $4");
								value = value.replaceAll("(\\d{4})\\s{2,}(\\d{4})\\s{2,}(\\d{4})\\s{2,}(\\d{3}[\\dX])", "$1 $2 $3 $4");
							} else 
							if(e.getKey().equalsIgnoreCase("BNF")){
								value = value.replaceAll("cb(\\d{8}[0-9bcdfghjkmnpqrstvwxz])", "$1");
							} else
							if(e.getKey().equalsIgnoreCase("NLA")){
								value = value.replaceAll("0000([1-9][0-9]{0,11})", "$1");
							} else
							if(e.getKey().equalsIgnoreCase("PLANTLIST") && e.getValue().matches("\\d+")){
								value = t.get("PREFIX")+"-"+e.getValue();
							} else
							if(e.getKey().equalsIgnoreCase("CANTIC")){
								value = value.replaceAll("(a\\d{7}[0-9x])\\/\\d+", "$1");
							} else
							if(e.getKey().equalsIgnoreCase("ORCID") && ! e.getValue().matches("0000-000(1-[5-9]|2-[0-9]|3-[0-4])\\d\\d\\d-\\d\\d\\d[\\dX]")){
								value = value.replaceAll("\\s+", "-");
							}
							
							try{
								if(value != null && value.matches(ac.getJSONObject(e.getKey()).getString("pattern"))){
									String formatter = (String) getWikidataConnection().request(new GetSpecificStatementRequest("P"+ac.getJSONObject(e.getKey()).getInt("property"), new Property(1630))).get(0).getClaim().getSnak().getValue();
									String u = formatter.replaceAll("\\$1", URLEncoder.encode(value, "UTF-8"));
									if(! reachable(new URL(u))){
										newParameters.remove(e.getKey());
										System.out.println("** 404 Error for "+e.getKey()+" value "+value+". ready for removal");
										continue;
									}
									String newu = detectRedirect(formatter, value);
									if(newu != null && newu.matches(ac.getJSONObject(e.getKey()).getString("pattern")) ){
										value = newu;
										System.out.println("** redirect for "+e.getKey()+" detected. new value: "+value+"");
									}
								}
							}catch(Exception e2){
								System.out.println("** unknown error while checking external databases for "+e.getKey()+": "+e2.getClass().getCanonicalName()+" "+e2.getMessage());
							}
							
							
							if(value == null || ! value.matches(ac.getJSONObject(e.getKey()).getString("pattern"))){
								System.out.println("** malformed value for "+e.getKey()+": "+value);
								newParameters.put(e.getKey(),e.getValue());
								if(e.getKey().equalsIgnoreCase("PLANTLIST") && t.containsKey("PREFIX")){newParameters.put("PREFIX", t.get("PREFIX"));}
								removable = false;
							}else{
								List<Statement> l = getConnection().request(new GetSpecificStatementRequest(base, new Property(ac.getJSONObject(e.getKey()).getInt("property"))));
								if(l.size() == 0){
									Statement s = getConnection().request(new CreateClaimRequest(base, new Claim(ac.getJSONObject(e.getKey()).getInt("property"), new StringSnak(value))));
									if(s == null){
										System.out.println("** unable to add claim for "+e.getKey());
										removable = false;
										newParameters.put(e.getKey(),e.getValue());
										if(e.getKey().equalsIgnoreCase("PLANTLIST") && t.containsKey("PREFIX")){newParameters.put("PREFIX", t.get("PREFIX"));}
									}else{
										getConnection().request(new SetReferenceRequest(s, config.getReference()));
										System.out.println("** added claim for "+e.getKey());
									}
								}else{
									boolean flag2 = false;
									String ss = "";
									for(Statement s : l){
										ss += ","+s.getClaim().getSnak().getValue();
										if(s.getClaim().getSnak().getValue().equals(value)){
											flag2 = true;
										}
									}
									ss = ss.substring(1);
									if(!flag2){
										System.out.println("** different value on wikidata for "+e.getKey()+": "+value+"!="+ss);
										newParameters.put(e.getKey(),e.getValue());
										if(e.getKey().equalsIgnoreCase("PLANTLIST") && t.containsKey("PREFIX")){newParameters.put("PREFIX", t.get("PREFIX"));}
									}
									removable = flag2 ? removable : false;
								}
							}
						}
					}
					
					if(getWikipediaConnection().request(new GetTemplatesValuesRequest(a, "bots")).size() !=  0 ){
						System.out.println("** bot-template found");
						continue;
					}
					
					removable = (newParameters.size() > 0 && newParameters.size() < t.size()) || removable;
					
					if(newParameters.size() >= t.size()){
						System.out.println("** no effective reduction possible");
						removable = false;
					}
					
					if(config.isLowerCaseMode()){
						HashMap<String,String> np2 = new HashMap<>();
						for(Entry<String,String> entry : newParameters.entrySet())
							np2.put(entry.getKey().toLowerCase(), entry.getValue());
						newParameters = np2;
					}
					
					if(removable){
						String old = getWikipediaConnection().request(new ContentRequest(a));
						String regex = "(?iu)\\{\\{\\ {0,1}(";
						for(String template : config.getTemplates()){
							regex += "("+Pattern.quote(template)+")|"; // 
						}
						regex = regex.substring(0, regex.length()-1);
						regex+= ")[^\\{\\}\\<\\>]+\\}\\}"; // [\\|\\p{L}\\d\\[\\]\\=\\_\\s\\ \\/\\\\\\-\\(\\)\\,\\.\\%\\+\\-]+
						
						
						String nw  = old.replaceAll(regex, "{{"+config.getTemplate()+(newParameters.size() > 0 ? "|"+convertToTemplateProperties(newParameters) : "")+"}}");
						if(nw.equals(old)){
							System.out.println("** unknown error: regex doesn't match");
							removable = false;
						}
						if(nw.length() == 0){
							System.out.println("** unknown error: error while calculating error");
							removable = false;
						}
						if(removable){
							getWikipediaConnection().request(new EditRequest(a, nw, config.getSummary()));
							System.out.println("** template replaced");
						}
					} 
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("** unknown error: "+e.getClass().getCanonicalName()+" "+e.getMessage());
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String convertToTemplateProperties(Map<String,String> m){
		String result = "";
		for(Entry<String, String> e : m.entrySet()){
			result +="|"+e.getKey()+"="+e.getValue()+" ";
		}
		if(result.length() > 0)
			result = result.substring(1).trim();
		return result;
	}
	
	private static boolean reachable(URL u) throws IOException{
		try{
			new GetRequest(u).request();
		}catch(FileNotFoundException e){
			return false;
		}
		return true;
	}
	
	private static String detectRedirect(String formatter, String identifier) throws MalformedURLException, UnsupportedEncodingException, IOException{
		String newidentifier = new GetRequest(formatter.replaceAll("\\$1", URLEncoder.encode(identifier, "UTF-8"))).detectRedirect().toExternalForm();
		
		formatter = formatter.replaceAll("\\$1", "(.+)")
				.replaceAll("https\\:\\/\\/","http[s]?://")
				.replaceAll("http\\:\\/\\/","http[s]?://");
		
		newidentifier = newidentifier.replaceAll(formatter, "$1");
		
		if(identifier.equals(newidentifier))
			return null;
		
		return newidentifier;
	}

}
