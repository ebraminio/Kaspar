package datasets.in;

import java.io.IOException;
import java.net.MalformedURLException;

import util.GetRequest;

public class GeoNames {

	
	public static String getCountryCode(double lat, double lng) throws MalformedURLException, IOException{
		return new GetRequest("http://api.geonames.org/countryCode?lat="+String.valueOf(lat)+"&lng="+String.valueOf(lng)+"&username=kaspar_wikidata").request();
	}
}
