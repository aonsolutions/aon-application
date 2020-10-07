package solutions.aon.in.invoice.templates;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmountParser {
	
	public static Collection<Double> getAmounts(String text) {
		char decimalSeparator = DecimalFormatSymbols.getInstance().getDecimalSeparator();
		char groupingSeparator = DecimalFormatSymbols.getInstance().getGroupingSeparator();
		
		Pattern pattern = Pattern.compile(
				 "(?<integ>-?\\+?(\\d+\\"+ groupingSeparator +")*\\d+)"
				+"\\" + decimalSeparator
				+"(?<fract>\\d+)"
				+"\\b"
				, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		List<Double> amounts = new ArrayList<Double>();
		
		Matcher matcher = pattern.matcher(text);
		int index = 0;
		while ( index <= text.length() && matcher.find(index) ) {
			String integ = matcher.group("integ");
			integ = integ
					.replaceAll("O", "0")
					.replaceAll(",", "");

			String fract = matcher.group("fract");
			System.out.print( "Parte Entera ..: {" + integ + "}" + "\t Parte Decimal .: {" + fract + "}"); 
			String str = integ + "." + fract;
			Double amount = null;
			try {
				amount = Double.parseDouble(str);
			} catch (NullPointerException | NumberFormatException  e ) {
			}
			
			if (amount != null) {
				int i = matcher.start();
				String prefix = substr(text,i-1, 1).toUpperCase();
				boolean fake = prefix.matches("[-,\\+\\.0-9]") || prefix.matches("[\\w]");
				System.out.print( fake ?" (FAKE 1!)":"" );
				if ( !fake ) {
					if ( (text.length() - matcher.end()) > 0 )  {
						String suffix = substr(text,matcher.end(), 1).toUpperCase();
						fake = suffix.matches("[-,\\+\\.0-9]") || suffix.matches("[\\w]");
						fake = fake || suffix.matches("[\\w]");
						System.out.print( fake ?" (FAKE 2!)":"" );
					}
				}
				if ( !fake ) {
					System.out.print( " (ADDED!)" );
					amounts.add(amount); 
				} 
				index = matcher.end() + 1 ;
			} else {
				index = matcher.start() + 1 ;
			}
			System.out.println();
		}
		return amounts;
	}
		
/*
	private static void insightAmounts(Collection<Double> collection, InvoiceBuilder<?> handler) {
		double percentages [] = {21.0, 10.0, 4.0};
		
		// const map: Map<number, number> = iamounts.reduce((m: Map<number, number>, n: number) => m.set(n, (m.get(n) || 0) + 1), new Map());
		Map<Double, Long> map = collection.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		// const amounts: number[] = Array.from(map.keys()).sort((a1, a2) => Math.abs(a2) - Math.abs(a1));
		Double amounts [] = collection.stream().sorted((a1,a2)-> Double.compare(Math.abs(a2), Math.abs(a1))).toArray(Double[]::new);
		
		iva: {
			for (int i = 0; i < amounts.length; i++) {
				double total = amounts[1];
			    //
		        // total = base + quota
		        // total = base + ( base * percentage / 100.00 )
		        // total = base ( 1 + ( 1 * percentage / 100.00 ))
		        // base = total /  ( 1 + percentage / 100.00 )
		        //
		        // yes you can replace +(..).toFixed(2) by Math.round(...*100)/100
		        //		
							
				for (double percentage: percentages ) {
					double base = total / (1 + percentage / 100.0);
					int indexOfBase = indexOf(amounts, base, i + 1);
					if ( indexOfBase >= 0 ) {
						double quota = total - base;
						int indexOfQuota = indexOf(amounts, quota, i+1);
						if ( indexOfQuota >= 0 ) {
//							handler.setTotal(total);
//							handler.setIVA(base, quota, percentage);
							break iva;
						} else {
							System.out.printf("IVA %f not found at %s :-( !!!!\r\n", quota, Arrays.stream(amounts).map( d -> String.valueOf(d)).collect(Collectors.joining(",")));
						}
					} else {
						System.out.printf("BASE %f not found at %s :-( !!!!\r\n", base, Arrays.stream(amounts).map( d -> String.valueOf(d)).collect(Collectors.joining(","))); 
					}
				}
			}
		}
		
		// �?
		// if (invoice.sender && invoice.sender.document_country && invoice.sender.document_country !== 'ES' && amounts.length > 0) {
		// 	invoice.total = Array.from(map.entries()).sort((e1: [number, number], e2: [number, number]) => e2[1] - e1[1] || e2[0] - e1[0])[0][0];
		// }

		// 'total = base + iva' not found try 'total - base'
	}
	
	private static int indexOf(Double amounts [], double amount, int start ) {
		double delta =(amount > 0.1 ? 0.019 : 0.0019);
		return indexOf(amounts, amount, start, delta);
	}

	private static int indexOf(Double amounts [], double amount, int start , double delta ) {
		int indexOf = -1;
		double diffOf = 0.5;
		for (int i = start; i < amounts.length; i++) {
			double diff = Math.abs(amounts[i] - amount);
			if ( diff < delta && diff < diffOf ) {
				indexOf = i;
				diffOf = diff;
			}
		}
		return indexOf;
	}
*/	
	private static String substr(String str, int start, int length) {
		int beginIndex = Math.max(start, 0);
		int endIndex = Math.min(str.length()+1, start+length);
		return str.substring(beginIndex, endIndex);
	}

	public static void main(String[] args) {
		Collection<Double> amounts = AmountParser.getAmounts(
			"1000.25"
		);
		for (Double amount : amounts) {
			System.out.println( amount );
		}
		System.out.println( "END" );
	}
}
