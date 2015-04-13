package mediawiki.info.wikibase;

public class WikibaseCoordinate {

	private Double latitude;
	private Double longitude;
	private Double altitude;
	private Double precision;
	private String globe;
	
	
	public WikibaseCoordinate(Double latitude2, Double longitude2,
			Double altitude2, Double precision2, String globe2) {
		latitude = latitude2;
		longitude = longitude2;
		altitude = altitude2;
		precision = precision2;
		globe = globe2;
	}
	
	public WikibaseCoordinate(Double lat, Double longit, int precision){
		this(lat,longit,null,Math.pow(10, -1d*precision),"http://www.wikidata.org/entity/Q2");
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public Double getAltitude() {
		return altitude;
	}
	public Double getPrecision() {
		return precision;
	}
	public String getGlobe() {
		return globe;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
	public void setPrecision(Double precision) {
		this.precision = precision;
	}
	public void setGlobe(String globe) {
		this.globe = globe;
	}

	
	public static WikibaseCoordinate parse(String lat, String lon){
		Double l1 = Double.parseDouble(lat);
		Double l2 = Double.parseDouble(lon);
		int precision = Math.min(lat.substring(lat.indexOf('.')+1).length(), lon.substring(lon.indexOf('.')+1).length());
		return new WikibaseCoordinate(l1, l2, precision);
	}
	
	@Override
	public String toString() {
		return latitude+":"+longitude;
	}
}
