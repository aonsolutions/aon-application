package solutions.aon.in.invoice.templates;

import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssueDateParser {
	
	public static Date getIssueDate(String text) {
		String[] patterns = {
				"Fecha emisi.n.*"+DateParser.DATES_PATTERN+"\\b",
				"Fecha de emisi.n.*"+DateParser.DATES_PATTERN+"\\b",
				"Fecha de emisi.n de factura.*"+DateParser.DATES_PATTERN+"\\b",
				"Fecha factura.*"+DateParser.DATES_PATTERN+"\\b",
				"Fecha de factura.*"+DateParser.DATES_PATTERN+"\\b",
				"Fecha de la factura.*"+DateParser.DATES_PATTERN+"\\b",
				"Fecha operaci.n.*"+DateParser.DATES_PATTERN+"\\b",
				"Fecha de env.o.*"+DateParser.DATES_PATTERN+"\\b",
				"Fecha.*factura.*"+DateParser.DATES_PATTERN+"\\b",
			};
		Date issueDate = null;
		for (String pat : patterns) {
			Pattern pattern = Pattern.compile( pat, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			int index = 0;
			while (index <= text.length() && matcher.find(index) ) {
				Collection<Date> dates = DateParser.getDates(matcher.group());
				if (dates != null && dates.size() > 0) {
					issueDate = dates.stream().findFirst().get();
					break;
				}
				index = matcher.end() + 1 ;				
			}
			if (issueDate != null) {
				break;
			}
		}
		return issueDate;
	}

	public static void main(String[] args) {
		Date date = IssueDateParser.getIssueDate(
			"Número de factura 21181119010327056\n"
			+"Fecha de emisión de factura 19 de noviembre de 2018\n"
			+"Fecha prevista de cargo 19/11/2018\n"
		);
		System.out.println( date );  
		System.out.println( "END" );
	}
}



