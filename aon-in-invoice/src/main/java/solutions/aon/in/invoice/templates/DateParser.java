package solutions.aon.in.invoice.templates;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateParser {
	
	public static Collection<Date> getDates(ParserContext ctx, String text) {
		List<Date> dates = new LinkedList<Date>();
		Pattern pattern = Pattern.compile( ctx.getDatePattern(), Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		int index = 0;
		while ( index <= text.length() && matcher.find(index) ) {
			boolean letterMonth = false;
			boolean added = false;
			Date date = null;
			String m = matcher.group("month");
			int month = -1;
			try { 
				month = Integer.parseInt(m); 
			} catch (NumberFormatException e) {
				m = m.replaceAll("\\W", "");
				if (ctx.getMonthsMap().containsKey((m.toLowerCase()))) {
					month = ctx.getMonthsMap().get(m.toLowerCase());
					letterMonth = true;
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
				if ( !fake && ctx.datesHasSameSeparator()) {
					String sep1 = matcher.group("sep1");
					sep1 = sep1 != null ? sep1.trim() : "";  
					String sep2 = matcher.group("sep2");
					sep2 = sep2 != null ? sep2.trim() : "";
					fake = !sep1.equals(sep2) || ("".equals(sep1) && !letterMonth);
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

	public static void main(String[] args) {
		Collection<Date> dates = DateParser.getDates(ParserContext.ENGLISH_US,
				"VAT Invoice Date:\n"+
				"083580179390\n"+
				"AON SOLUTIONS, S.L.\n"+
				"Duque de Wellington 52 Bajo\n"+
				"Vitoria-Gasteiz, Álava, 01010, ES\n"+
				"EUINES21-1701\n"+
				"VAT Invoice Date: February 3, 2021"
			);
		for (Date date : dates) {
			System.out.println( date );
		}
		System.out.println( "END" );
	}
}



