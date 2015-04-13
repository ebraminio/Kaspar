package main;

import java.net.URL;
import java.util.Locale;

import javax.activation.MimeType;

import org.apache.http.entity.mime.MIME;

import datasets.in.Flinfo;

import mediawiki.WikimediaConnection;
import mediawiki.request.LoginRequest;
import mediawiki.request.UploadRequest;

public class TestCommons {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		Locale.setDefault(Locale.GERMANY);
		
		WikimediaConnection wikidata = new WikimediaConnection("commons","wikimedia.org");
		
		
		String id 			="21439318";
		String filename 	="Free State Stadium2.jpg";
		
		
		String flinfo = Flinfo.getFileDescription("panoramio", id);
		System.out.println(flinfo);
		wikidata.request(new UploadRequest(filename, new URL("http://static.panoramio.com/photos/original/"+id+".jpg").openStream(), flinfo, "Imported from Panoramio"));
	}

}
