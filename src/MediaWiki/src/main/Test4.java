package main;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import mediawiki.WikimediaConnection;
import mediawiki.info.Article;
import mediawiki.info.wikibase.Property;
import mediawiki.info.wikibase.snaks.StringSnak;
import mediawiki.request.GetTemplatesValuesRequest;
import mediawiki.request.LoginRequest;
import mediawiki.request.RollbackRequest;
import mediawiki.request.TemplateEmbeddedInRequest;
import mediawiki.request.wikibase.CreateClaimRequest;
import mediawiki.request.wikibase.GetSpecificStatementRequest;
import mediawiki.request.wikibase.SetDescriptionRequest;
import mediawiki.request.wikibase.SetLabelRequest;
import mediawiki.task.AdministrativeDivisionTask;
import mediawiki.task.GeoNamesStateTask;

public class Test4 {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		Locale.setDefault(Locale.GERMANY);
		
		
		String username = Config.USERNAME;
		String password = Config.PASSWORD;
		
		WikimediaConnection wikidata = new WikimediaConnection("www","wikidata.org");
		wikidata.request(new LoginRequest(username, password));
		wikidata.setBot(true);
		
		String[] props = new String[]{
				"p214",
				"p244",
				"p213",
				"p496",
				"p227",
				"p906",
				"p269",
				"p268",
				"p651",
				"p245",
				"p434",
				"p409",
				"p349",
				"p1015",
				"p1053",
				"P650"
		};
		
		String[] names = new String[]{
				"VIAF",
				"LCCN",
				"ISNI",
				"ORCID",
				"GND",
				"SELIBR",
				"SUDOC",
				"BNF",
				"BPN",
				"ULAN",
				"MBA",
				"NLA",
				"NDL",
				"BIBSYS",
				"RID",
				"RKDartists"

		};
		
		JSONObject o = new JSONObject();
		for(int i = 0; i < props.length; i++){
			JSONObject o2 = new JSONObject();
			o2.put("property", props[i].substring(1));
			o2.put("pattern", wikidata.request(new GetSpecificStatementRequest(props[i], new Property(1793))).get(0).getClaim().getSnak().getValue());
			o.put(names[i], o2);
		}
		
		System.out.println(o.toString(1));
		
		
	}

}
