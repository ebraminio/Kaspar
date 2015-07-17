package mediawiki.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javat.xml.Element;

import datasets.in.GND;

import mediawiki.WikidataQuery;
import mediawiki.MediaWikiConnection;
import mediawiki.MediaWikiTask;
import mediawiki.info.wikibase.Claim;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.Reference;
import mediawiki.info.wikibase.Statement;
import mediawiki.info.wikibase.WikibaseDate;
import mediawiki.info.wikibase.snaks.DateSnak;
import mediawiki.info.wikibase.snaks.ItemSnak;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.SetReferenceRequest;

public class GNDReferencingTask extends MediaWikiTask{

	public GNDReferencingTask(MediaWikiConnection con) {
		super(con);
	}

	@Override
	public void run() {
		WikidataQuery wdq = new WikidataQuery("CLAIM[31:5] AND CLAIM[227]");
		System.out.println("daten abgerufen");
		SimpleDateFormat log = new SimpleDateFormat("HH:mm:ss");
		try {
			for(Integer i : wdq.request()){
				try{
					String base = "Q"+i;
					String gnd = (String) getConnection().request(new GetSpecificStatementRequest(base, new Property(227))).get(0).getClaim().getSnak().getValue();
					Reference ref = new Reference();
					ref.addClaim(new Claim(248, 36578));
					ref.addClaim(new Claim(new Property(813), new DateSnak(new WikibaseDate(WikibaseDate.ONE_DAY))));
					Element e = GND.getGNDEntry(gnd);
					if(e.getChildren("gender").size() == 0)
						continue;
					ItemSnak gender = null;
					switch(e.getChildren("gender").get(0).getAttribute("resource").getValue()){
					case "http://d-nb.info/standards/vocab/gnd/Gender#male" :
						gender = new ItemSnak(6581097);
						break;
					case "http://d-nb.info/standards/vocab/gnd/Gender#female" :
						gender = new ItemSnak(6581072);
						break;
					}
					if(gender != null){
						List<Statement> l = getConnection().request(new GetSpecificStatementRequest(base, new Property(21)));
						for(Statement s : l){
							if(s.getClaim().getSnak().equals(gender) && !s.hasReferences()){
								getConnection().request(new SetReferenceRequest(s, ref));
								System.out.println(Thread.currentThread().getName()+"\t["+log.format(new Date())+"]\t"+base+" reference added");
							}
						}
					}
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
