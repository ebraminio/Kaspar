package mediawiki.info.wikibase.snaks;

import javat.xml.Element;

import mediawiki.info.wikibase.Snak;
import mediawiki.info.wikibase.WikibaseDate;

import org.json.JSONException;
import org.json.JSONObject;


public class DateSnak extends Snak<WikibaseDate> {

	public DateSnak(WikibaseDate value) {
		super(value);
	}

	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("time", getValue().getFormattedDate());
		o.put("timezone", getValue().getTimezone());
		o.put("before", getValue().getBefore());
		o.put("after", getValue().getAfter());
		o.put("precision", getValue().getPrecision());
		o.put("calendarmodel", getValue().getCalendarmodel());
		return o;
	}

	@Override
	public boolean equalsXML(Element e) {
		if(! e.getAttribute("type").getValue().equals("time"))
			return false;
		if(! e.getAttribute("time").getValue().equals(getValue().getFormattedDate()))
			return false;
		if(! e.getAttribute("timezone").getValue().equals(getValue().getTimezone()+""))
			return false;
		if(! e.getAttribute("before").getValue().equals(getValue().getBefore()+""))
			return false;
		if(! e.getAttribute("after").getValue().equals(getValue().getAfter()+""))
			return false;
		if(! e.getAttribute("precision").getValue().equals(getValue().getPrecision()+""))
			return false;
		if(! e.getAttribute("calendarmodel").getValue().equals(getValue().getCalendarmodel()))
			return false;
		return true;
	}

	@Override
	public JSONObject toReferenceRepresentation() throws JSONException {
		JSONObject o = new JSONObject();
		o.put("type", "time");
		o.put("value", toJSONObject());
		return o;
	}

	@Override
	public JSONObject toClaimRepresentation() throws JSONException {
		return toJSONObject();
	}

	@Override
	public void convert(Element e) throws Exception {
		e = e.getChildren("value").get(0);
		String time = e.getAttribute("time").getValue();
		int timezone = Integer.parseInt(e.getAttribute("timezone").getValue());
		int before = Integer.parseInt(e.getAttribute("before").getValue());
		int after = Integer.parseInt(e.getAttribute("after").getValue());
		int precision = Integer.parseInt(e.getAttribute("precision").getValue());
		String calendarmodel = e.getAttribute("calendarmodel").getValue();
		
		setValue(new WikibaseDate(time, timezone, before, after, precision, calendarmodel));
	}

}
