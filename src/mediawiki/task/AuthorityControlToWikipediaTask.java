package mediawiki.task;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import mediawiki.WikimediaConnection;
import mediawiki.info.wikibase.Sitelink;
import mediawiki.request.ContentRequest;
import mediawiki.request.EditRequest;
import mediawiki.request.GetTemplatesValuesRequest;
import mediawiki.request.wikibase.GetSitelinkRequest;

public class AuthorityControlToWikipediaTask extends WikipediaWikidataTask {


	public AuthorityControlToWikipediaTask(WikimediaConnection wikidata,
			WikimediaConnection wikipedia) {
		super(wikidata, wikipedia);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		try {
			Scanner s = new Scanner(new URL("https://tools.wmflabs.org/autolist/index.php?language=en&project=wikipedia&category=Wikipedia%20articles%20with%20authority%20control%20information&depth=12&wdq=%28CLAIM%5B227%5D%20OR%20CLAIM%5B214%5D%20OR%20CLAIM%5B244%5D%20OR%20CLAIM%5B213%5D%20OR%20CLAIM%5B496%5D%20OR%20CLAIM%5B906%5D%20OR%20CLAIM%5B269%5D%20OR%20CLAIM%5B268%5D%20OR%20CLAIM%5B651%5D%20OR%20CLAIM%5B245%5D%20OR%20CLAIM%5B434%5D%20OR%20CLAIM%5B409%5D%20OR%20CLAIM%5B349%5D%20OR%20CLAIM%5B1015%5D%20OR%20CLAIM%5B1053%5D%20OR%20CLAIM%5B650%5D%29%20AND%20LINK%5Benwiki%5D&statementlist=&run=Run&mode_manual=or&mode_cat=not&mode_wdq=and&mode_find=or&chunk_size=10000&download=1").openStream());
			while(s.hasNextLine()){
				try {
					String base = s.nextLine();
					Sitelink sl = getWikidataConnection().request(new GetSitelinkRequest(base,"enwiki"));
					if(getWikipediaConnection().request(new GetTemplatesValuesRequest(sl.getTitle(),"Authority control")).size() > 0)
						continue;
					if(getWikipediaConnection().request(new GetTemplatesValuesRequest(sl.getTitle(), "bots")).size() !=  0 ){
						System.out.println("** bot-template found");
						continue;
					}
					String content = getWikipediaConnection().request(new ContentRequest(sl.getTitle()));
					boolean flag = true;
					for(String token : new String[]{"{{Persondata", "{{DEFAULTSORT:", "[[Category:"}){
						if(content.indexOf(token) > -1){
							content = content.substring(0,content.indexOf(token))+"{{Authority control}}\n"+content.substring(content.indexOf(token));
							flag = false;
							break;
						}
					}
					if(flag){
						content += "\n{{Authority control}}";
					}
					getWikipediaConnection().request(new EditRequest(sl.getTitle(), content, "embed authority control with wikidata information"));
					System.out.println(sl.getTitle()+" edited");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
