package main;

import java.util.Locale;

import mediawiki.WikimediaConnection;
import mediawiki.request.LoginRequest;
import mediawiki.task.NormdatenTask2;

public class Main {

	public static void main(String...args) throws Exception{
		
		Locale.setDefault(Locale.GERMANY);
		
		WikimediaConnection wikidata = new WikimediaConnection("www","wikidata.org");
		wikidata.request(new LoginRequest(Config.USERNAME, Config.PASSWORD));
		wikidata.setBot(true);
		
		WikimediaConnection wikipedia = new WikimediaConnection("en","wikipedia.org");
		
		
		Thread.currentThread().setName("Normdaten");
		
		
		new NormdatenTask2(wikidata, wikipedia).run();
		
		
	}
}
