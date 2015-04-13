package util;

public class HTMLStripper {

	public static String stripHTML(String html){
		return html.replaceAll("\\<.*?\\>", "");
	}
	
}
