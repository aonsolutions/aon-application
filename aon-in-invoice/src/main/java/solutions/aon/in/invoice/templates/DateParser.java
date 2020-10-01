package solutions.aon.in.invoice.templates;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {
	
	public static Collection<Date> getDates(String text) {
		List<Date> dates = new ArrayList<Date>();
	
		// let re: RegExp = /(0?[1-9]|[12][0-9]|3[01])\s*[-/]\s*(0?[1-9]|1[012])\s*[-/]\s*(20\d{2}|([12][0-9]))/gim;
		Pattern pattern = Pattern.compile(
				"(0?[1-9]|[12][0-9]|3[01])"
				+"\\s*"
				+"[-/\\.]"
				+"\\s*"
				+"(0?[1-9]|1[012])"
				+"\\s*"
				+"[-/\\.]"
				+"\\s*"
				+"(20\\d{2}|([12][0-9]))"
				, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Collection<String[]> found = findGroups(text, pattern);
		for ( String strs [] : found ) {
			// const day: number = parseInt(match[1], 10);
			// const month: number = parseInt(match[2], 10) - 1;
			// const year: number = match[4] ? parseInt(`20${match[4]}`, 10) : parseInt(match[3], 10);
			
			int day = Integer.parseInt(strs[1]);
			int month = Integer.parseInt(strs[2]);
			int year = strs[4] != null ? Integer.parseInt("20"+strs[4]) : Integer.parseInt(strs[3]);
			
			Date date = getDate(day, month, year);

			dates.add(date);
		}
		
	    Map<String,Integer> months = new HashMap<String,Integer>() {
			private static final long serialVersionUID = 1065328755275830021L;
			{
	    	      put("enero", 1);
	    	      put("en", 1);
	    	      put("ene", 1);
	    	      put("febrero", 2);
	    	      put("feb", 2);
	    	      put("marzo", 3);
	    	      put("mar", 3);
	    	      put("abril", 4);
	    	      put("abr", 4);
	    	      put("mayo", 5);
	    	      put("may", 5);
	    	      put("junio", 6);
	    	      put("jun", 6);
	    	      put("julio", 7);
	    	      put("jul", 7);
	    	      put("agosto", 8);
	    	      put("ag", 8);
	    	      put("ago", 8);
	    	      put("agto", 8);
	    	      put("septiembre", 9);
	    	      put("sep", 9);
	    	      put("sept", 9);
	    	      put("octubre", 10);
	    	      put("oct", 10);
	    	      put("noviembre", 11);
	    	      put("nov", 11);
	    	      put("diciembre", 12);
	    	      put("dic", 12);
	    	}
	    };		
		
		// re = /(0?[1-9]|[12][0-9]|3[01])\s*(?:de|\/)\s*([a-z]+\.?)\s*(?:de|\/)\s*(20\d{2}|([12][0-9]))/gim;
	    //pattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])\\s*(?:de|\\/)\\s*([a-z]+\\.?)\\s*(?:de|\\/)\\s*(20\\d{2}|([12][0-9]))", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		pattern  =  Pattern.compile(
					"(0?[1-9]|[12][0-9]|3[01])"			// DAY
					+"\\s*"
					+"(?:de|\\/)?"						// DE ?
					+"\\s*"
					+"([a-z]+\\.?)"						// MONTH (LETRA)
					+"\\s*"
					+"(?:de|:del|\\/)?"					// DE|DEL ?
					+"\\s*"
					+"(20\\d{2}|([12][0-9]))"			// YEAR
				, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		found = findGroups(text, pattern);
		for ( String strs [] : found ) {
			// const day: number = parseInt(match[1], 10);
			// const month: number = months[match[2].toLowerCase()];
			// const year: number = match[4] ? parseInt(`20${match[4]}`, 10) : parseInt(match[3], 10);
			if (months.containsKey((strs[2].toLowerCase()))) {
				int day = Integer.parseInt(strs[1]);
				int month = months.get(strs[2].toLowerCase());
				int year = strs[4] != null ? Integer.parseInt("20"+strs[4]) : Integer.parseInt(strs[3]);
				Date date = getDate(day, month, year);
				dates.add(date);
			}
		}
		
		return dates;
	}

	private static Date getDate(int day, int month, int year) {
		return Date.from(LocalDateTime.of(year, month, day, 0, 0).atZone(ZoneId.systemDefault()).toInstant());
	}
		
	private static Collection<String[]> findGroups(String text, Pattern pattern ) {
		List<String[]> found = new ArrayList<String[]>();
		
		Matcher matcher = pattern.matcher(text);
		while ( matcher.find() ) {
			int groupCount = matcher.groupCount() + 1;
			String groups [] = new String[groupCount];
			for ( int i = 0; i < groupCount; i++ ) {
				groups[i] = matcher.group(i);
			}
			found.add(groups);
		}
		
		return found;
	}
}
