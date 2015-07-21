package mediawiki.task;

import mediawiki.MediaWikiConnection;
import mediawiki.info.Article;
import mediawiki.request.ContentRequest;
import mediawiki.request.EditRequest;
import mediawiki.request.GetTemplatesValuesRequest;
import mediawiki.request.TranscludedTemplatesRequest;
import mediawiki.task.config.ACtWPConfiguration;
import mediawiki.task.config.ACtWPSelector;

public class AuthorityControlToWikipediaTask extends WikipediaWikidataTask {

	private ACtWPConfiguration config;
	
	public AuthorityControlToWikipediaTask(MediaWikiConnection wikidata,
			MediaWikiConnection wikipedia, ACtWPConfiguration config) {
		super(wikidata, wikipedia);
		this.config = config;
	}

	@Override
	public void run() {
		ACtWPSelector selector = config.getSelector();
		selector.prepare(getWikidataConnection(), getWikipediaConnection());
		try {
			selector.fetch();
			while(selector.hasNext()){
				try{
					Article a = selector.next();
					System.out.println(a.getTitle());
					if(getWikipediaConnection().request(new TranscludedTemplatesRequest(a, "Template:"+config.getTemplate())).size() > 0)
						continue;
					if(getWikipediaConnection().request(new TranscludedTemplatesRequest(a, "Template:bots")).size() !=  0 ){
						System.out.println("** bot-template found");
						continue;
					}
					String content = getWikipediaConnection().request(new ContentRequest(a));
				/*	for(String t : config.getAliases()) 
						if(content.matches("(?iu).*\\{\\{"+t+"\\}\\}.*"))
							continue; */
					content = config.getPositioner().insert(content, config.getTemplate());
					getWikipediaConnection().request(new EditRequest(a, content, config.getSummary()));
					System.out.println(a.getTitle()+" edited");
				}catch(Exception e2){
					e2.printStackTrace();
				}
			}
			selector.close();
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

}
