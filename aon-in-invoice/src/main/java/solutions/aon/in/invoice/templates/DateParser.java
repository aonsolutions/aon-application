package solutions.aon.in.invoice.templates;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {
	
	public static Collection<Date> getDates(String text) {
		List<Date> dates = new LinkedList<Date>();
		Pattern pattern = Pattern.compile(
				 "(?<day>0?[1-9]|[12][0-9]|3[01])"
				+"(?:[-|/|\\.|\\p{Blank}*|\\h*])"
				+"(?:de|\\/)?"
				+"(?:\\p{Blank}*)"
				+"(?<month>0?[1-9]|1[012]|[a-z]+\\.?)"
				+"(?:[-|/|\\.|\\p{Blank}*|\\h*])"
				+"(?:de|del|\\/)?"
				+"(?:\\p{Blank}*|\\h*)"
				+"(?<year>20\\d{2}|[12][0-9])"
				, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		int index = 0;
		while ( index <= text.length() && matcher.find(index) ) {
			boolean added = false;
			Date date = null;
			String m = matcher.group("month");
			int month = -1;
			try { 
				month = Integer.parseInt(m); 
			} catch (NumberFormatException e) {
				if (MONTHS.containsKey((m.toLowerCase()))) {
					month = MONTHS.get(m.toLowerCase());
				}
			};
			if (month != -1) {
				int day = Integer.parseInt(matcher.group("day"));
				String y = matcher.group("year");
				int year = ( y != null && y.length() == 2) ? Integer.parseInt("20"+y) : Integer.parseInt(y);
				date = getDate(day, month, year);
			}
			if (date != null) {
				int i = matcher.start();
				int preffixOffset =  i>=0?1:0;
				String prefix = substr(text,i-preffixOffset, preffixOffset).toUpperCase();
				boolean fake = i >= 4 && prefix.matches("[A-Z0-9]"); 
				if ( !fake ) {
					if ( (text.length() - matcher.end()) > 0 )  {
						String suffix = substr(text,matcher.end(), 1).toUpperCase();
						boolean match = suffix.matches("[-_,;\\)\\]\\s\\.]");
						fake = !match;
					}
				}
				if ( !fake ) {
					dates.add(date);
					added = true;
					
				}
			}
			index = (added?matcher.end():matcher.start()) + 1;
		}
		return dates;
	}

	private static Date getDate(int day, int month, int year) {
		return Date.from(LocalDateTime.of(year, month, day, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
	}

	private static String substr(String str, int start, int length) {
		int beginIndex = Math.max(start, 0);
		int endIndex = Math.min(str.length()+1, start+length);
		return str.substring(beginIndex, endIndex);
	}

    private static Map<String,Integer> MONTHS = new HashMap<String,Integer>() {
		private static final long serialVersionUID = 1065328755275830021L;
		{
    	      put("en", 1);
    	      put("ene", 1);
    	      put("ener", 1);
    	      put("enero", 1);
    	      put("feb", 2);
    	      put("febr", 2);
    	      put("febre", 2);
    	      put("febrer", 2);
    	      put("febrero", 2);
    	      put("mar", 3);
    	      put("marz", 3);
    	      put("marzo", 3);
    	      put("abr", 4);
    	      put("abri", 4);
    	      put("abril", 4);
    	      put("may", 5);
    	      put("mayo", 5);
    	      put("jun", 6);
    	      put("juni", 6);
    	      put("junio", 6);
    	      put("jul", 7);
    	      put("juli", 7);
    	      put("julio", 7);
    	      put("ag", 8);
    	      put("ago", 8);
    	      put("agos", 8);
    	      put("agto", 8);
    	      put("agost", 8);
    	      put("agosto", 8);
    	      put("sep", 9);
    	      put("sept", 9);
    	      put("septi", 9);
    	      put("septie", 9);
    	      put("septiem", 9);
    	      put("septiemb", 9);
    	      put("septiembr", 9);
    	      put("septiembre", 9);
    	      put("oct", 10);
    	      put("octu", 10);
    	      put("octub", 10);
    	      put("octubr", 10);
    	      put("octubre", 10);
    	      put("nov", 11);
    	      put("novi", 11);
    	      put("novie", 11);
    	      put("noviem", 11);
    	      put("noviemb", 11);
    	      put("noviembr", 11);
    	      put("noviembre", 11);
    	      put("dic", 12);
    	      put("dici", 12);
    	      put("dicie", 12);
    	      put("diciem", 12);
    	      put("diciemb", 12);
    	      put("diciembr", 12);
    	      put("diciembre", 12);
    	}
    };		

	public static void main(String[] args) {
		Collection<Date> dates = DateParser.getDates(
			"Madrid, 19 Noviembre 2018 Factura TA5ZH0179306 Teléfono: 945290461 Página 1/2"
		);
		for (Date date : dates) {
			System.out.println( date );
		}
		System.out.println( "END" );
	}
}



