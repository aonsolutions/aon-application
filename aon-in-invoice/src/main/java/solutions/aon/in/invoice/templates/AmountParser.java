package solutions.aon.in.invoice.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import solutions.aon.in.invoice.InvoiceBuilder;

public class AmountParser {
	
	public static Collection<Double> getAmounts(String text) {
		// const re: RegExp = /((-\s*)?\d+\s*([\.|,]\s*(\d)+){1,2}-?)/gim;
		Pattern pattern = Pattern.compile("((-\\s*)?\\d+\\s*([\\.|,]\\s*(\\d)+){1,2}-?)", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Collection<String> found = find(text, pattern);
		List<Double> amounts = new ArrayList<Double>();
		for ( String str : found ) {
			// XXX XX,XX => XXXXX,XX
			// let str: string = match[0].replace(/\s/g, '').replace(/(.*)-$/, '-$1'); // postfix
			str = str.replaceAll("\\s", "").replaceAll("(.*)-$", "-$1");

			// XXX.XXX,XX => XXXXXX.XX
			// if (str.match(/,/)) {
			//	str = str.replace(/\./g, '').replace(/,/g, '.');
			// }
			if ( str.contains(",")) {
				str = str.replaceAll("\\.", "").replaceAll(",", ".");
			}
				
			// clean mismatched
		    // if (/\d+\.\d+\.\d+/g.test(str)) {
		    //    continue;
		    // }
			if ( str.matches("\\d+\\.\\d+\\.\\d+") ) {
				continue;
			}
			
			// if (/\d+\.\d{3}/g.test(str)) {
			//   continue;
			// }
			if ( str.matches("\\d+\\.\\d{3}") ) {
				continue;
			}
			
			try {
				double amount = Double.parseDouble(str);
				amounts.add(amount);
			} catch (NullPointerException | NumberFormatException  e ) {
				
			}
		}
		return amounts;
	}

	private static Collection<String> find(String text, Pattern pattern ) {
		List<String> found = new ArrayList<String>();
		
		Matcher matcher = pattern.matcher(text);
		while ( matcher.find() ) {
			found.add(matcher.group());
		}
		
		return found;
	}
	
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
		
		// ¿?
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
	

}
