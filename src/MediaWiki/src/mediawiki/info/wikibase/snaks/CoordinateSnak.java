package mediawiki.info.wikibase.snaks;

import javat.xml.Element;

import org.json.JSONException;
import org.json.JSONObject;

import mediawiki.info.wikibase.Snak;
import mediawiki.info.wikibase.WikibaseCoordinate;

public class CoordinateSnak extends Snak<WikibaseCoordinate> {

	public CoordinateSnak(WikibaseCoordinate value) {
		super(value);
	}
	
	public CoordinateSnak(Double lat, Double lon,int precision){
		this(new WikibaseCoordinate(lat, lon, precision));
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("latitude", getValue().getLatitude());
		o.put("longitude", getValue().getLongitude());
		o.put("altitude", getValue().getAltitude());
		o.put("precision", getValue().getPrecision());
		o.put("globe", getValue().getGlobe());
		return o;
	}

	@Override
	public boolean equalsXML(Element e) {
		if(! e.getAttribute("type").getValue().equals("globecoordinate"))
			return false;
		e = e.getChildren("value").get(0);
		if(! e.getAttribute("latitude").getValue().equals(getValue().getLatitude()+""))
			return false;
		if(! e.getAttribute("longitude").getValue().equals(getValue().getLongitude()+""))
			return false;
		if(! e.getAttribute("altitude").getValue().equals(getValue().getAltitude()+""))
			return false;
		if(! e.getAttribute("precision").getValue().equals(getValue().getPrecision()+""))
			return false;
		if(! e.getAttribute("globe").getValue().equals(getValue().getGlobe()))
			return false;
		return true;
	}

	@Override
	public JSONObject toReferenceRepresentation() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("type", "globecoordinate");
		o.put("value", toJSONObject());
		return o;
	}

	@Override
	public JSONObject toClaimRepresentation() throws JSONException {
		return toJSONObject();
	}

	@Override
	public void convert(Element element) throws Exception {
		Element e = element.getChildren("value").get(0);
		double latitude = Double.parseDouble(e.getAttribute("latitude").getValue());
		double longitude = Double.parseDouble(e.getAttribute("longitude").getValue());
		Double altitude = e.getAttribute("altitude").getValue().equals("") ? null : Double.parseDouble(e.getAttribute("altitude").getValue());
		double precision = Double.parseDouble(e.getAttribute("precision").getValue());
		String globe = e.getAttribute("globe").getValue();
		
		setValue(new WikibaseCoordinate(latitude, longitude, altitude, precision, globe));
		
	}

}
