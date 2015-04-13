package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import mediawiki.WikimediaConnection;
import mediawiki.request.LoginRequest;
import mediawiki.request.wikibase.SetLabelRequest;


public class Test5 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String username = Config.USERNAME;
		String password = Config.PASSWORD;
		
		Locale.setDefault(Locale.GERMANY);
		
		WikimediaConnection wikidata = new WikimediaConnection("www","wikidata.org");
		wikidata.request(new LoginRequest(username, password));
		wikidata.setBot(true);
		
		Scanner s = new Scanner(new File("/home/tim/Arbeitsfläche/quarry-2889-untitled-run16265.json"));
		StringBuffer b = new  StringBuffer();
		while(s.hasNext())
			b.append(s.nextLine());
		JSONObject o = new JSONObject(b.toString());
		ArrayList<JSONArray> a = new ArrayList<>();
		String last = null;
		int no = 1;
		for(int i = 0; i < o.getJSONArray("rows").length(); i++) {
			JSONArray a1 = o.getJSONArray("rows").getJSONArray(i);
			if(a1.getString(2).contains("ko") )
				continue;
			if(a1.getString(2).contains("ru") )
				continue;
			if(last != null && ! last.equals(a1.getString(0)) && a.size() > 1){
				String[] langfalsch = null;
				String richtig = null;
				int richtiglangs = 0;
				for(JSONArray a2 : a){
					if(a2.getString(1).contains("?")){
						langfalsch = a2.getString(2).split("\\,");
					}else{
						if(richtig != null){
							if(a2.getString(2).split("\\,").length > richtiglangs){
								richtig = a2.getString(1);
								richtiglangs = a2.getString(2).split("\\,").length;
							}
						}else{
							richtig = a2.getString(1);
							richtiglangs = a2.getString(2).split("\\,").length;
						}
					}
				}
				if(richtig == null)
					continue;
				System.out.println(no+". "+last+" "+richtig+" "+Arrays.deepToString(langfalsch));
				for(String lang : langfalsch){
					wikidata.request(new SetLabelRequest(last, lang, richtig));
					System.out.println("Label für "+lang+": "+richtig);
				}
				no++;
				a.clear();
			}
			a.add(a1);
			last = a1.getString(0);
		}
	}

}
