package solutions.aon.in.invoice.templates;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TotalParser {
	
	private static final Locale ES = new Locale("es");
	private static final String DS = Character.toString(DecimalFormatSymbols.getInstance( ES ).getDecimalSeparator());
	private static final String GS = Character.toString(DecimalFormatSymbols.getInstance( ES ).getGroupingSeparator());
	private static final String INTEGER_KEY = "integ";
	private static final String FRACTION_KEY = "fract";
	private static final String DP = "(?<"+INTEGER_KEY+">-?\\+?(\\d+\\"+GS+")*\\d+)"+"\\"+DS+"(?<"+FRACTION_KEY+">\\d+)";
	private static final Pattern DP_PATTERN = Pattern.compile( DP , Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
	
	public static Double getTotal(String text) {
		String[] patterns = {
			"total\\s+pagar.*"+DP+"\\b",
			"total\\s+a\\s+pagar.*"+DP+"\\b",
			"total\\s+importe\\s+factura.*"+DP+"\\b",
			"total\\s+factura.*"+DP+"\\b"
//			"total\\s+"+DP+"\\b"
		};
		
		Double retAmount = null;
		for (String pat : patterns) {
			Pattern pattern = Pattern.compile( pat , Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				Matcher mat = DP_PATTERN.matcher(matcher.group());
				if (mat.find()) {
					String tot = mat.group();
					tot = tot.replaceAll("O", "0")
							 .replaceAll( "\\" + GS , "")
					 		 .replaceAll( "\\" + DS , ".");
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
		String text = "Get JetBrains Toolbox with its 15+ code editors for "
				+ "all languages and technologies included in one app. Total Factura 2.105,50 "
				+ "all languages and technologies included in one "
				;
		Double total = TotalParser.getTotal( text );
		System.out.println( "Total..: "  + total );
		System.out.println( "END" );
	}
}
