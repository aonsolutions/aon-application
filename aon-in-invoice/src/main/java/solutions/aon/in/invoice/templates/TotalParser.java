package solutions.aon.in.invoice.templates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TotalParser {
	
	public static Double getTotal(ParserContext ctx , String text) {
		Double retAmount = null;
		Pattern decimalPattern = Pattern.compile( ctx.getDecimalPattern() , Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		for (String pat : ctx.getTotalPatterns()) {
			Pattern pattern = Pattern.compile( pat , Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				Matcher mat = decimalPattern.matcher(matcher.group());
				if (mat.find()) {
					String tot = mat.group();
					tot = tot.replaceAll("O", "0")
							 .replaceAll( "\\" + ctx.getDecimalGroupingSeparator() , "")
					 		 .replaceAll( "\\" + ctx.getDecimalSeparator() , ".");
					try {
						retAmount = Double.parseDouble(tot);
						break;
					} catch (NullPointerException | NumberFormatException  e ) {
					}
				}
			}
		}
		return retAmount;
	}
	
	public static void main(String[] args) {
		String text = "Total 2.105,50\u20AC";
		Double total = TotalParser.getTotal( ParserContext.SPANISH, text );
		System.out.println( "Total..: "  + total );
		System.out.println( "END" );
	}
}
