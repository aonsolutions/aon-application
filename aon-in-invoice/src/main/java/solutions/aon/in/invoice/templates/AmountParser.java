package solutions.aon.in.invoice.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmountParser {
	
	public static List<Double> getAmounts(ParserContext ctx, String text) {
		String ds = ctx.getDecimalSeparator();
		String gs = ctx.getDecimalGroupingSeparator();
		String pat = "(?<integ>-?\\+?(\\d+\\"+gs+")*\\d+)"
				+"\\"+ds
				+"(?<fract>\\d+)"
				+"\\b"; 
//		System.out.println( pat );
		
		Pattern pattern = Pattern.compile( pat , Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		List<Double> amounts = new ArrayList<Double>();
		
		Matcher matcher = pattern.matcher(text);
		int index = 0;
		while ( index <= text.length() && matcher.find(index) ) {
			String integ = matcher.group("integ");
			integ = integ.replaceAll("O", "0")
						 .replaceAll( "\\" + ctx.getDecimalGroupingSeparator(), "");
			String fract = matcher.group("fract");
//			System.out.print( 
//				"("+ matcher.start() +", "+ matcher.end() +") Parte Entera ..: {" + integ + "}" + "  Parte Decimal .: {" + fract + "} --- ["
//				+ AonStringUtils.substring( text, matcher.start(), matcher.end()) + "]" 
//			); 
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
//				System.out.print( fake ?" (FAKE 1!)":"" );
				if ( !fake ) {
					if ( (text.length() - matcher.end()) > 0 )  {
						String suffix = substr(text,matcher.end(), 1).toUpperCase();
						fake = suffix.matches("[-\\"+gs+"\\+\\"+ds+"0-9]") || suffix.matches("[\\w]");
						fake = fake || suffix.matches("[\\w]");
//						System.out.print( fake ?" (FAKE 2!)":"" );
					}
				}
				if ( !fake ) {
//					System.out.print( " (ADDED!)" );
					amounts.add(amount); 
				} 
				index = matcher.end() + 1 ;
			} else {
				index = matcher.start() + 1 ;
			}
//			System.out.println();
		}
		return amounts;
	}
		
	private static String substr(String str, int start, int length) {
		int beginIndex = Math.max(start, 0);
		int endIndex = Math.min(str.length()+1, start+length);
		return str.substring(beginIndex, endIndex);
	}

	public static void main(String[] args) {
		String text = "Total Factura 2.105,50\u20ac";;
		Collection<Double> amounts = AmountParser.getAmounts( ParserContext.SPANISH, text );
		
		System.out.println( "Amounts..:" );
		for (Double amount : amounts) {
			System.out.println( amount );
		}
		System.out.println( "END" );
	}
}
