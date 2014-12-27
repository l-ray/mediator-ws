package de.clubspot.mediator.criteria;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlDateWrapper {

	private Date dStart;
	
	public UrlDateWrapper(Date dStart) {
		this.dStart = dStart;
	}
	
	public String getUrl(String sUrl){

		Pattern p = Pattern.compile("\\{startTimestamp\\}");
		Matcher m = p.matcher(sUrl);
		sUrl = m.replaceAll(String.valueOf(this.dStart.getTime()/1000));
		
		p = Pattern.compile("\\{startDay\\}");
		m = p.matcher(sUrl);
		sUrl = m.replaceAll(this.simpleFormatDate("dd"));
		
		p = Pattern.compile("\\{startMonth\\}");
		m = p.matcher(sUrl);
		sUrl = m.replaceAll(this.simpleFormatDate("MM"));
		
		p = Pattern.compile("\\{startYear\\}");
		m = p.matcher(sUrl);
		sUrl = m.replaceAll(this.simpleFormatDate("yyyy"));
		
		return sUrl;
	}
	
	private String simpleFormatDate(String pattern) {
		// TODO: mit Datenbank-Eintrag (bspw. de_DE) in jeweiligem Pattern verknuepfen
		SimpleDateFormat fmt = new SimpleDateFormat(pattern, new java.util.Locale("de","DE"));
		return fmt.format(this.dStart);
	}
}
