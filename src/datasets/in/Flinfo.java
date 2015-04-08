package datasets.in;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import util.GetRequest;

public class Flinfo {

	
	public static String getFileDescription(String repo, String id) throws MalformedURLException, UnsupportedEncodingException, IOException{
		return new GetRequest("http://wikipedia.ramselehof.de/flinfo.php?id="+URLEncoder.encode(id, "UTF-8")+"&repo="+URLEncoder.encode(repo, "UTF-8")+"&raw=on").request();
	}
}
