package mediawiki.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.json.JSONObject;


import datasets.in.VIAF;

import mediawiki.WikimediaConnection;
import mediawiki.WikimediaTask;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.WikibaseDate;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.wikibase.GetClaimsRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;

import static main.GNDLoad.addClaim;

public class VIAFLinkTask extends WikimediaTask {

	private Connection connect;
	
	private final int version = 1;
	private final String summary = "adding %s identifier %s based on VIAF";
	
	public VIAFLinkTask(WikimediaConnection con, Connection connect) {
		super(con);
		this.connect = connect;
	}

	@Override
	public void run() {
		try{
			ResultSet r = connect.createStatement().executeQuery("SELECT * FROM gnddata ORDER BY viaflinkversion ASC, ID DESC");
			PreparedStatement p = connect.prepareStatement("UPDATE gnddata SET viaflinkversion = '"+version+"' WHERE wikibase = ?");
			while(r.next()){
				try{
					ArrayList<Statement> as = (ArrayList<Statement>) getConnection().request(new GetSpecificStatementRequest(r.getString("wikibase"), new Property(214)));
					if(as.size() == 0)
						continue;
					for(Statement s : as){
						String viaf = (String) s.getClaim().getSnak().getValue();
						
						Reference ref = new Reference();
						ref.addClaim(new Claim(248, 54919));
						ref.addClaim(new Claim(new Property(813), new DateSnak(new WikibaseDate(WikibaseDate.ONE_DAY))));
					//	ref.addClaim(new Claim(new Property(214), new StringSnak(viaf)));
						
						JSONObject o = VIAF.getLinks(viaf);
						
						if(o.has("SUDOC")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(269),new StringSnak(o.getJSONArray("SUDOC").getString(0))), ref, String.format(summary, "SUDOC",o.getJSONArray("SUDOC").getString(0) ));
						}
						
						if(o.has("BAV")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(1017),new StringSnak(o.getJSONArray("BAV").getString(0))), ref, String.format(summary, "BAV",o.getJSONArray("BAV").getString(0) ));
						}
						
						if(o.has("ISNI")){
							String isni = o.getJSONArray("ISNI").getString(0);
							isni = isni.substring(0,4)+" "+isni.substring(4,8)+" "+isni.substring(8,12)+" "+isni.substring(12);
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(213),new StringSnak(isni)), ref, String.format(summary, "ISNI", isni ));
						}
						
						if(o.has("LC")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(244),new StringSnak(o.getJSONArray("LC").getString(0))), ref, String.format(summary, "LC",o.getJSONArray("LC").getString(0) ));
						}
						
						if(o.has("NLI")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(949),new StringSnak(o.getJSONArray("NLI").getString(0))), ref, String.format(summary, "NLI",o.getJSONArray("NLI").getString(0) ));
						}
						
						if(o.has("NTA")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(1006),new StringSnak(o.getJSONArray("NTA").getString(0))), ref, String.format(summary, "NTA",o.getJSONArray("NTA").getString(0) ));
						}
						
						if(o.has("BNF")){
							String bnf = o.getJSONArray("BNF").getString(0);
							bnf = bnf.substring("http://catalogue.bnf.fr/ark:/12148/cb".length());
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(268),new StringSnak(bnf)), ref, String.format(summary, "BNF",bnf ));
						}
						
						if(o.has("EGAXA")){
							String bnf = o.getJSONArray("EGAXA").getString(0);
							bnf = bnf.substring("vtls".length());
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(1309),new StringSnak(bnf)), ref, String.format(summary, "EGAXA", bnf));
						}
						
						if(o.has("LAC")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(1670),new StringSnak(o.getJSONArray("LAC").getString(0))), ref, String.format(summary, "LAC",o.getJSONArray("LAC").getString(0) ));
						}
						
						if(o.has("LNB")){
							String bnf = o.getJSONArray("LNB").getString(0);
							bnf = bnf.substring("LNC10-".length());
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(1368),new StringSnak(bnf)), ref, String.format(summary, "LNB", bnf ));
						}
						
						if(o.has("NDL")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(349),new StringSnak(o.getJSONArray("NDL").getString(0))), ref, String.format(summary, "NDL",o.getJSONArray("NDL").getString(0) ));
						}
						
						if(o.has("NLA")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(409),new StringSnak(o.getJSONArray("NLA").getString(0))), ref, String.format(summary, "NLA",o.getJSONArray("NLA").getString(0) ));
						}
						
						if(o.has("NLP")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(1695),new StringSnak(o.getJSONArray("NLP").getString(0).toUpperCase())), ref, String.format(summary, "NLP",o.getJSONArray("NLP").getString(0).toUpperCase() ));
						}
						
						if(o.has("NSK")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(1375),new StringSnak(o.getJSONArray("NSK").getString(0))), ref, String.format(summary, "NSK",o.getJSONArray("NSK").getString(0) ));
						}
						
				/*		if(o.has("NUKAT")){
							String bnf = o.getJSONArray("NUKAT").getString(0);
							bnf = "n"+bnf.substring("vtls".length());
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(1207),new StringSnak(bnf)), ref, summary);
						} */
						
						if(o.has("PTBNP")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(1005),new StringSnak(o.getJSONArray("PTBNP").getString(0))), ref, String.format(summary, "PTBNP",o.getJSONArray("PTBNP").getString(0) ));
						}
						
						if(o.has("SELIBR")){
							addClaim(getConnection(), r.getString("wikibase"), new Claim(new Property(906),new StringSnak(o.getJSONArray("SELIBR").getString(0))), ref, String.format(summary, "SELIBR",o.getJSONArray("SELIBR").getString(0) ));
						}
					}
					p.setString(1, r.getString("wikibase"));
					p.executeUpdate();
				}catch(Exception e2){
					e2.printStackTrace();
					continue;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
