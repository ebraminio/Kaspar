package mediawiki.info.wikibase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WikibaseDate {
	
	public final static int ONE_BILLION_YEARS = 0;
	public final static int HUNDRET_MILLION_YEARS = 1;
	public final static int TEN_MILLION_YEARS = 2;
	public final static int ONE_MILLION_YEARS = 3;
	public final static int HUNDRET_THOUSAND_YEARS = 4;
	public final static int TEN_THOUSAND_YEARS = 5;
	public final static int ONE_THOUSAND_YEARS = 6;
	public final static int ONE_HUNDRET_YEARS = 7;
	public final static int TEN_YEARS = 8;
	public final static int ONE_YEAR = 9;
	public final static int ONE_MONTH = 10;
	public final static int ONE_DAY = 11;
	public final static int ONE_HOUR = 12;
	public final static int ONE_MINUTE = 13;
	public final static int ONE_SECOND = 14;
	
	private Date date = null;
	
	private int timezone;
	private int before;
	private int after;
	private int precision;
	private String calendarmodel;
	
	public WikibaseDate(int precision){
		this(new Date(),TimeZone.getDefault().getRawOffset()/1000/60,0,0,precision);
	}
	
	public WikibaseDate(Date date, int tz, int before, int after, int precision, String calendarmodel){
		setDate(date);
		setTimezone(tz);
		setBefore(before);
		setAfter(after);
		setPrecision(precision);
		setCalendarmodel(calendarmodel);
	}
	
	public WikibaseDate(Date date, int tz, int before, int after, int precision){
		this(date,tz,before,after,precision,"http://www.wikidata.org/entity/Q1985727");
	}
	
	public WikibaseDate(String date, int tz, int before, int after, int precision, String calendarmodel) throws ParseException{
		this(new Date(),tz,before,after,precision,calendarmodel);
		
		switch(date.charAt(0)){
		case '+' : date = "n. Chr. "+date.substring(1); break;
		case '-' : date = "v. Chr. "+date.substring(1); break;
		}
		SimpleDateFormat d = new SimpleDateFormat(	"GGGG yyyyyyyyyyy-MM-dd'T'HH:mm:ss'Z'");
		TimeZone z = (TimeZone) TimeZone.getDefault().clone();
		z.setRawOffset(tz*60*1000);
		d.setTimeZone(z);
		setDate(d.parse(date));
	}
	
	public String getFormattedDate(){
		SimpleDateFormat d2 = new SimpleDateFormat(	"GGGG yyyyyyyyyyy-MM-dd'T'"); // HH:mm:ss'Z'
		String d = d2.format(getDate());
		switch(d.charAt(0)){
		case 'n' : d = "+"+d.substring(8); break;
		case 'v' : d = "-"+d.substring(8); break;
		}
		return d+"00:00:00Z"; // TODO Unterstützung für Stunde/Minunten/Sekunden Angaben
	}

	public Date getDate() {
		return date;
	}

	public int getTimezone() {
		return timezone;
	}

	public int getBefore() {
		return before;
	}

	public int getAfter() {
		return after;
	}

	public int getPrecision() {
		return precision;
	}

	public String getCalendarmodel() {
		return calendarmodel;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	public void setBefore(int before) {
		this.before = before;
	}

	public void setAfter(int after) {
		this.after = after;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public void setCalendarmodel(String calendarmodel) {
		this.calendarmodel = calendarmodel;
	}
	
	@Override
	public String toString() {
		return getFormattedDate();
	}

	// TODO Fertigstellen
	public static WikibaseDate parseWikipediaDate(String wiki) throws ParseException {
		wiki = wiki.replaceAll("\\&nbsp\\;", " ");
		String months = "Januar|Februar|März|April|Mai|Juni|Juli|August|September|Oktober|November|Dezember";
		if(wiki.matches("[0-9]{1,4}")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_YEAR);
		}else if(wiki.matches("[0-9]{1,4} [nv]{1}\\. Chr\\.")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy GGGG");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_YEAR);
		}else if(wiki.matches("[0-9]{1,2}\\. ("+months+") [0-9]{1,4}")){
			String[] ms = months.split("\\|");
			for(int i = 0; i < ms.length; i++){
				wiki = wiki.replaceAll(ms[i], (i+1)+"");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd. MM yyyy");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_DAY);
		}else if(wiki.matches("[0-9]{1,2}\\. ("+months+") [0-9]{1,4} [nv]{1}\\. Chr\\.")){
			String[] ms = months.split("\\|");
			for(int i = 0; i < ms.length; i++){
				wiki = wiki.replaceAll(ms[i], (i+1)+"");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("dd. MM yyyy GGGG");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_DAY);
		}else if(wiki.matches("[0-9]{1,2}\\. Jahrhundert")){
			wiki = wiki.replaceAll("\\. Jahrhundert", "00");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_HUNDRET_YEARS);
		}else if(wiki.matches("[0-9]{1,2}\\. Jahrhundert [nv]{1}\\. Chr\\.")){
			wiki = wiki.replaceAll("\\. Jahrhundert", "00");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy GGGG");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_HUNDRET_YEARS);
		}else if(wiki.matches("("+months+") [0-9]{1,4}")){
			String[] ms = months.split("\\|");
			for(int i = 0; i < ms.length; i++){
				wiki = wiki.replaceAll(ms[i], (i+1)+"");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MM yyyy");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_MONTH);
		}else if(wiki.matches("("+months+") [0-9]{1,4} [nv]{1}\\. Chr\\.")){
			String[] ms = months.split("\\|");
			for(int i = 0; i < ms.length; i++){
				wiki = wiki.replaceAll(ms[i], (i+1)+"");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MM yyyy GGGG");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_MONTH);
		}else if(wiki.matches("[0-9]{1,2}\\. Jahrtausend [nv]{1}\\. Chr\\.")){
			wiki = wiki.replaceAll("\\. Jahrtausend", "000");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy GGGG");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_THOUSAND_YEARS);
		}else if(wiki.matches("[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2}")){
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_DAY);
		}else if(wiki.matches("[0-9]{1,2}\\.[0-9]{1,2}\\.[0-9]{1,2} [nv]{1}\\. Chr\\.")){
			SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy GGGG");
			return new WikibaseDate(sdf.parse(wiki), 0, 0, 0, ONE_DAY);
		}
		return null;
	}

}

