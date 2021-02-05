package solutions.aon.in.invoice.templates;

import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssueDateParser {
	
	public static Date getIssueDate(ParserContext ctx, String text) {
		String[] patterns = ctx.getIssueDatePatterns();;
		Date issueDate = null;
		for (String pat : patterns) {
			Pattern pattern = Pattern.compile( pat, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			int index = 0;
			while (index <= text.length() && matcher.find(index) ) {
				Collection<Date> dates = DateParser.getDates(ctx, matcher.group());
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
		Date date = IssueDateParser.getIssueDate( ParserContext.ENGLISH_US,
				"VAT Invoice Date:\n"+
				"083580179390\n"+
				"AON SOLUTIONS, S.L.\n"+
				"Duque de Wellington 52 Bajo\n"+
				"Vitoria-Gasteiz, Álava, 01010, ES\n"+
				"EUINES21-1701\n"+
				"VAT Invoice Date: February 3, 2021"
		);
		System.out.println( date );  
		System.out.println( "END" );
	}
}



