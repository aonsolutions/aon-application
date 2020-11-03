package solutions.aon.in.invoice.templates;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TotalParser {
	
	public static List<Double> getAmounts(String text) {
		Locale ES = new Locale("es");
		String ds = Character.toString(DecimalFormatSymbols.getInstance( ES ).getDecimalSeparator());
		String gs = Character.toString(DecimalFormatSymbols.getInstance( ES ).getGroupingSeparator());

		String digitPattern = "(?<integ>-?\\+?(\\d+\\"+gs+")*\\d+)"+"\\"+ds+"(?<fract>\\d+)";
		
		String[] patterns = {
			"total.+pagar.*"+digitPattern+"\\b",
			"total.+factura.*"+digitPattern+"\\b",
			"total.*"+digitPattern+"\\b"
		};
		
		List<Double> amounts = new ArrayList<Double>();
		boolean added = false;
		for (String pat : patterns) {
			Pattern pattern = Pattern.compile( pat , Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			int index = 0;
			while (index <= text.length() && matcher.find(index) ) {
				String integ = matcher.group("integ");
				integ = integ.replaceAll("O", "0")
							 .replaceAll( "\\" + gs , "");
				String fract = matcher.group("fract");
				String str = integ + "." + fract;
				Double amount = null;
				try {
					amount = Double.parseDouble(str);
				} catch (NullPointerException | NumberFormatException  e ) {
				}
				
				if (amount != null) {
					int i = matcher.start();
					String prefix = substr(text,i-1, 1).toUpperCase();
					boolean fake = prefix.matches("[-\\"+gs+"\\+\\"+ds+"0-9]") || prefix.matches("[\\w]");
					if ( !fake ) {
						if ( (text.length() - matcher.end()) > 0 )  {
							String suffix = substr(text,matcher.end(), 1).toUpperCase();
							fake = suffix.matches("[-\\"+gs+"\\+\\"+ds+"0-9]") || suffix.matches("[\\w]");
							fake = fake || suffix.matches("[\\w]");
						}
					}
					if ( !fake ) {
						added = true;
						amounts.add(amount);
					} 
					index = matcher.end() + 1 ;
				} else {
					index = matcher.start() + 1 ;
				}
			}
			if (added) break;
		}
		return amounts;
	}
		
	private static String substr(String str, int start, int length) {
		int beginIndex = Math.max(start, 0);
		int endIndex = Math.min(str.length()+1, start+length);
		return str.substring(beginIndex, endIndex);
	}

	public static void main(String[] args) {
		String text = "TOTAL IMPORTE FACTURA: -835,95 € Dirección de suministro:  C/ COBALTO, 21  47012 VALLADOLID";
				;
		Collection<Double> totals = TotalParser.getAmounts( text );
		System.out.println( "Totals..:" );
		for (Double total : totals) {
			System.out.println( total );
		}
		System.out.println( "END" );
	}
}
