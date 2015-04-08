package mediawiki;

public class WikimediaUtil {

	
	public static String formatLCCN(String lccn){
		String[] parts = lccn.split("\\/");
		String flccn = parts[0]+parts[1];
		for(int i = 0; i < 6-parts[2].length(); i++){
			flccn += "0";
		}
		flccn += parts[2];
		return flccn;
	}
}
