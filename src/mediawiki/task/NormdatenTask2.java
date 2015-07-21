package mediawiki.task;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mediawiki.ContinuingRequest;
import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiException;
import mediawiki.MediaWikiUtil;
import mediawiki.info.Article;
import mediawiki.info.Project;
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
import mediawiki.request.TranscludedTemplatesRequest;
import mediawiki.request.WikiBaseItemRequest;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.SetReferenceRequest;
import mediawiki.task.config.NormdatenTask2Configuration;
import mediawiki.task.config.NormdatenTask2ErrorHandler;

import org.json.JSONObject;

import util.GetRequest;

import datasets.in.GND;
import datasets.in.VIAF;

public class NormdatenTask2 extends WikipediaWikidataTask {

	
	private NormdatenTask2Configuration config;
	private HashSet<NormdatenTask2ErrorHandler> handlers = new HashSet<>();
	
	public NormdatenTask2(MediaWikiConnection wikidata, MediaWikiConnection wikipedia, NormdatenTask2Configuration c){
		super(wikidata, wikipedia);
		
		config = c;
	}

	@Override
	public void run() {
		List<Article> articles;
		try {
			
			InputStream in = NormdatenTask2Configuration.class.getResourceAsStream("authoritycontrol.json");
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
		/*	if(config.getRequest() instanceof ContinuingRequest)
				((ContinuingRequest) config.getRequest()).setLimit(10); */
			
			articles = getWikipediaConnection().request(config.getRequest()); 
			System.out.println(articles.size()+" Artikel geladen");
			
			for(Article a : articles){
				System.out.println("* [["+a.getTitle()+"]] "+new Date().toGMTString());
				try{
					
					String base = (String) getWikipediaConnection().request(new WikiBaseItemRequest(a));
					if(base == null){
						throwWarning(new NormdatenTask2Exception(a, "no wikidata item", NormdatenTask2ExceptionLevel.PROBLEM));
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
						throwWarning(new NormdatenTask2Exception(a, "unknown alias embedded or recursive transclusion", NormdatenTask2ExceptionLevel.PROBLEM));
						continue;
					}
					
					if(t2.size() > 1){
						throwWarning(new NormdatenTask2Exception(a, "more than one template embedded", NormdatenTask2ExceptionLevel.PROBLEM));
						continue;
					}
					Map<String, String> t = t2.get(0);
					
					if(t.size() == 0){
						throwWarning(new NormdatenTask2Exception(a, "already moved to wikidata", NormdatenTask2ExceptionLevel.INFO));
						continue;
					}
						
					boolean removable = true;
					HashMap<String,String> newParameters = new HashMap<>();
					
					if(t.containsKey("WORLDCAT") && ! t.containsKey("WORLDCATID")) {
						t.put("WORLDCATID", t.get("WORLDCAT"));
						t.remove("WORLDCAT");
					}
					
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
						if(MediaWikiUtil.containsPersianDigits(e.getValue()))
							e.setValue(MediaWikiUtil.parsePersianNumber(e.getValue()));
						
						if(e.getKey().equalsIgnoreCase("WORLDCATID") && e.getValue().trim().length() > 0) {
							if(! reachable(new URL("http://www.worldcat.org/identities/"+URLEncoder.encode(e.getValue().replaceAll("\\/",""),"UTF-8")))) {
								newParameters.remove(e.getKey());
								System.out.println("** 404 Error for "+e.getKey()+" value "+e.getValue()+". ready for removal");
								continue;
							}
							if(e.getValue().matches("^lccn-.*")){
								String lccn = null;
								if(t.containsKey("LCCN"))
									lccn = t.get("LCCN");
								else{
									List<Statement> l = getWikidataConnection().request(new GetSpecificStatementRequest(base, new Property(ac.getJSONObject("LCCN").getInt("property"))));
									if(l.size() > 0)
										lccn = (String) l.get(0).getClaim().getSnak().getValue();
								}
								if(lccn == null && e.getValue().matches("^lccn-.*$")){
									String value = e.getValue().replaceAll("^lccn-", "");
									if(! value.matches(ac.getJSONObject("LCCN").getString("pattern"))){
										value = value.replaceAll("\\-", "/");
										value = value.replaceAll("^(|n|nb|nr|no|ns|sh|sj|sn)(.*)$", "$1/$2");
										value = MediaWikiUtil.formatLCCN(value);
									}
									if(value != null && value.matches(ac.getJSONObject("LCCN").getString("pattern")) ) {
										Statement s = getConnection().request(new CreateClaimRequest(base, new Claim(ac.getJSONObject("LCCN").getInt("property"), new StringSnak(value))));
										if(s == null){
											throwWarning(new NormdatenTask2Exception(a, "unable to add claim",e.getKey(), NormdatenTask2ExceptionLevel.INTERNAL));
											removable = false;
											newParameters.put(e.getKey(),e.getValue());
											continue;
										}else{
											getConnection().request(new SetReferenceRequest(s, config.getReference()));
											System.out.println("** added claim for "+e.getKey());
											continue;
										}
									}else{
										newParameters.put(e.getKey(),e.getValue());
										continue;
									}
								}else{
									String[] lccns = MediaWikiUtil.splitLCCN(lccn);
									if(! reachable(new URL("http://www.worldcat.org/identities/lccn-"+URLEncoder.encode(lccns[0]+"-"+lccns[1]+"-"+lccns[2], "UTF-8")))) {
										newParameters.remove(e.getKey());
										System.out.println("** 404 Error for "+e.getKey()+" value "+lccns[0]+"-"+lccns[1]+"-"+lccns[2]+". ready for removal");
										continue;
									}
									for(int i = 0; i < lccns.length; i++) 
										lccns[i] = lccns[i].replaceAll("^0+(\\d+)$", "$1");
									if(e.getValue().matches("^lccn-"+Matcher.quoteReplacement(lccns[0])+"\\-?0*"+Matcher.quoteReplacement(lccns[1])+"\\-?0*"+Matcher.quoteReplacement(lccns[2])+"$" )) {
										newParameters.remove(e.getKey());
										System.out.println("** WORLDCATID equals suggested value. ready for removal");
										continue;
									}else{
										throwWarning(new NormdatenTask2Exception(a, "different value on wikidata", e.getKey()+": "+e.getValue()+"!=lccn-"+lccns[0]+"-"+lccns[1]+"-"+lccns[2], NormdatenTask2ExceptionLevel.PROBLEM));
										newParameters.put(e.getKey(),e.getValue());
										continue;
									}
								}
							}else{
								throwWarning(new NormdatenTask2Exception(a, "WORLDCATID not based on LCCN", NormdatenTask2ExceptionLevel.PROBLEM));
								newParameters.put(e.getKey(),e.getValue());
								removable = false;
								continue;
							}
						}
						if(! ac.has(e.getKey()) && (e.getValue().trim().length() > 0 || (config.isKeepEmpty() && e.getValue().trim().length() == 0) ) ){
							throwWarning(new NormdatenTask2Exception(a, "unknown template property", e.getKey(), NormdatenTask2ExceptionLevel.PROBLEM));
							newParameters.put(e.getKey(),e.getValue());
							removable = false;
						}else{
							if(e.getValue().trim().length() == 0){
								if(config.isKeepEmpty()){
									newParameters.put(e.getKey(),"");
									removable = false;
									throwWarning(new NormdatenTask2Exception(a, "keep-empty-mode. empty template property: "+e.getKey(), NormdatenTask2ExceptionLevel.INFO));
								}
								continue;
							}
							String value = e.getKey().equals("LCCN") && ! e.getValue().matches(ac.getJSONObject(e.getKey()).getString("pattern")) ? MediaWikiUtil.formatLCCN(e.getValue()) : e.getValue();
							if(e.getKey().equals("LCCN") && value == null)
								value = e.getValue();
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
								}
							}catch(Exception e2){
								throwWarning(new NormdatenTask2Exception(a, "unknown error while checking external databases", e.getKey()+": "+e2.getClass().getCanonicalName()+" "+e2.getMessage(), NormdatenTask2ExceptionLevel.EXTERNAL));
							}
							
							
							if(value == null || ! value.matches(ac.getJSONObject(e.getKey()).getString("pattern"))){
								throwWarning(new NormdatenTask2Exception(a, "malformed value", e.getKey()+": "+value, NormdatenTask2ExceptionLevel.PROBLEM));
								newParameters.put(e.getKey(),e.getValue());
								if(e.getKey().equalsIgnoreCase("PLANTLIST") && t.containsKey("PREFIX")){newParameters.put("PREFIX", t.get("PREFIX"));}
								removable = false;
							}else{
								List<Statement> l = getConnection().request(new GetSpecificStatementRequest(base, new Property(ac.getJSONObject(e.getKey()).getInt("property"))));
								if(l.size() == 0){
									Statement s = getConnection().request(new CreateClaimRequest(base, new Claim(ac.getJSONObject(e.getKey()).getInt("property"), new StringSnak(value))));
									if(s == null){
										throwWarning(new NormdatenTask2Exception(a, "unable to add claim", e.getKey(), NormdatenTask2ExceptionLevel.INTERNAL));
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
									HashSet<String> wikidatavalues = new HashSet<>();
									for(Statement s : l){
										ss += ","+s.getClaim().getSnak().getValue();
										wikidatavalues.add(s.getClaim().getSnak().getValue().toString());
										if(s.getClaim().getSnak().getValue().equals(value)){
											flag2 = true;
										}
									}
									ss = ss.substring(1);
									if(!flag2) {
										try{
											String formatter = (String) getWikidataConnection().request(new GetSpecificStatementRequest("P"+ac.getJSONObject(e.getKey()).getInt("property"), new Property(1630))).get(0).getClaim().getSnak().getValue();
											String newu = detectRedirect(formatter, value);
											if(newu != null && newu.matches(ac.getJSONObject(e.getKey()).getString("pattern")) ){
												throwWarning(new NormdatenTask2Exception(a, "redirect for "+e.getKey()+" detected", "new value: "+value+"", NormdatenTask2ExceptionLevel.INFO));
												if(wikidatavalues.contains(newu)){
													flag2 = true;
												}
											}
										}catch(Exception e2){
											throwWarning(new NormdatenTask2Exception(a, "unknown error while checking external databases",  e.getKey()+": "+e2.getClass().getCanonicalName()+" "+e2.getMessage(), NormdatenTask2ExceptionLevel.EXTERNAL));
										}
									}
									if(!flag2){
										throwWarning(new NormdatenTask2Exception(a, "different value on wikidata", e.getKey()+": "+value+"!="+ss, NormdatenTask2ExceptionLevel.PROBLEM));
										newParameters.put(e.getKey(),e.getValue());
										if(e.getKey().equalsIgnoreCase("PLANTLIST") && t.containsKey("PREFIX")){newParameters.put("PREFIX", t.get("PREFIX"));}
									}
									removable = flag2 ? removable : false;
								}
							}
						}
					}
					
					if(getWikipediaConnection().request(new TranscludedTemplatesRequest(a, "Template:bots")).size() !=  0 ){
						throwWarning(new NormdatenTask2Exception(a, "bot-template found", NormdatenTask2ExceptionLevel.PROBLEM));
						continue;
					}
					
					removable = (newParameters.size() > 0 && newParameters.size() < t.size()) || removable;
					
					if(newParameters.size() >= t.size()){
						throwWarning(new NormdatenTask2Exception(a, "no effective reduction possible", NormdatenTask2ExceptionLevel.FINAL));
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
						regex+= ")[^\\{\\}\\<\\>]+\\}\\}";
						
						
						String nw  = old.replaceAll(regex, "{{"+config.getTemplate()+(newParameters.size() > 0 ? "|"+convertToTemplateProperties(newParameters) : "")+"}}");
						if(nw.equals(old)){
							throwWarning(new NormdatenTask2Exception(a, "regex doesn't match", NormdatenTask2ExceptionLevel.PROBLEM));
							removable = false;
						}
						if(nw.length() == 0){
							throwWarning(new NormdatenTask2Exception(a, "error while calculating", NormdatenTask2ExceptionLevel.INTERNAL));
							removable = false;
						}
						if(removable){
							if(getWikipediaConnection().isTestState()){
								throwWarning(new NormdatenTask2Exception(a, "template can only be replaced manually", NormdatenTask2ExceptionLevel.PROBLEM));
							}
							try{
								getWikipediaConnection().request(new EditRequest(a, nw, config.getSummary()));
								throwWarning(new NormdatenTask2Exception(a, "template replaced", NormdatenTask2ExceptionLevel.FINAL));
								System.out.println("** template replaced");
							}catch(MediaWikiException e3){
								throwWarning(new NormdatenTask2Exception(a, "edit request rejected", NormdatenTask2ExceptionLevel.PROBLEM));
							}
						}
					} 
				}catch(Exception e){
					e.printStackTrace();
					throwWarning(new NormdatenTask2Exception(a, "unknown error", e.getClass().getCanonicalName()+" "+e.getMessage(), NormdatenTask2ExceptionLevel.INTERNAL));
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private HashMap<NormdatenTask2Exception, Integer> stat = new HashMap<>();
	
	private void throwWarning(NormdatenTask2Exception e) throws Exception{
		System.out.println("** "+e.getMessage());
		if(stat.containsKey(e))
			stat.put(e, stat.get(e)+1);
		else
			stat.put(e, 1);
		handleError(e);
	}
	
	private void throwException(NormdatenTask2Exception e) throws Exception{
		throwWarning(e);
		throw e;
	}
	
	public Map<NormdatenTask2Exception, Integer> getStatistic() {
		return stat;
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

	public class NormdatenTask2Exception extends MediaWikiException {
		
		private Article article;
		private String type;
		private NormdatenTask2ExceptionLevel level;
		private String message = null;
		
		public NormdatenTask2Exception(Article a, String type, String message, NormdatenTask2ExceptionLevel level) {
			super(type+ " ("+ message+") at "+a.getTitle());
			setArticle(a);
			this.type = type;
			this.level = level;
			this.message = message;
		}
		
		public NormdatenTask2Exception(Article a, String type, NormdatenTask2ExceptionLevel level) {
			super(type+ " at "+a.getTitle());
			setArticle(a);
			this.type = type;
			this.level = level;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null)
				return false;
			if(! (obj instanceof NormdatenTask2Exception))
				return false;
			NormdatenTask2Exception e = (NormdatenTask2Exception) obj;
			return this.type.equals(e.type);
		}
		
		@Override
		public int hashCode() {
			return type.hashCode();
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	
		public Project getProject() throws MalformedURLException {
			return NormdatenTask2.this.getWikipediaConnection().getProject();
		}

		public Article getArticle() {
			return article;
		}

		public void setArticle(Article article) {
			this.article = article;
		}

		public NormdatenTask2ExceptionLevel getLevel() {
			return level;
		}

		public void setLevel(NormdatenTask2ExceptionLevel level) {
			this.level = level;
		}
		
		public String getSimpleMessage(){
			return message;
		}
	}
	
	public enum NormdatenTask2ExceptionLevel {
		INFO, PROBLEM, INTERNAL, EXTERNAL, FINAL
	}

	public void registerErrorHandler(NormdatenTask2ErrorHandler e){
		handlers.add(e);
	}
	
	protected void handleError(NormdatenTask2Exception e) throws Exception{
		for(NormdatenTask2ErrorHandler eh : handlers)
			if(eh.accept(e))
				eh.handle(e);
	}
	
	public void removeErrorHandler(NormdatenTask2ErrorHandler e){
		handlers.remove(e);
	}
}
