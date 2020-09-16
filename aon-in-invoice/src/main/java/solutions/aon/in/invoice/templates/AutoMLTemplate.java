package solutions.aon.in.invoice.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.UnknownInvoiceException;

public class AutoMLTemplate extends AbstractTemplate {
	
	private static class Nif {
		
		private static enum NifType {
			CIF,
			DNI,
			NIE,
			NIF
			
		}
		
		private String str;
		private NifType type;
		
		
		public Nif(String str, NifType type) {
			super();
			this.str = str;
			this.type = type;
		}
		
		private static Nif parse(String str) {
			if (isCif(str)) {
				return new Nif(str, NifType.CIF);
			}
			else if (isDni(str)) {
				return new Nif(str, NifType.DNI);
			}
			else if (isNie(str)) {
				return new Nif(str, NifType.NIE);
			}
			else if (isNif(str)) {
				return new Nif(str, NifType.NIF);
			}
			return null;
		}


		private static boolean isDni(String str) {
			// Nif.is(str, /^(\d{8})([A-HJ-NP-TV-Z])$/)
			return is(str,"^(\\d{8})([A-HJ-NP-TV-Z])$");
		}
		

		private static boolean isNif(String str) {
			// Nif.is(str, /^[KLM](\d{7})([A-HJ-NP-TV-Z])$/)
			return is(str,"^[KLM](\\d{7})([A-HJ-NP-TV-Z])$");
		}
		
		private static boolean isNie(String str) {
			// const match: RegExpMatchArray | null = str.toUpperCase().match(/^([XYZ])(\d{7})([A-HJ-NP-TV-Z])$/);
			Matcher matcher = Pattern.compile("^([XYZ])(\\d{7})([A-HJ-NP-TV-Z])$").matcher(str.toUpperCase());
			if ( matcher.matches() ) {
				// const xyz = { X: 0, Y: 1, Z: 2 };
				Map<Character,Integer> xyz = new HashMap<Character, Integer>(){
					{
						put('X',0);
						put('Y',1);
						put('Z',2);
					}
				};
		        // return 'TRWAGMYFPDXBNJZSQVHLCKE'[+(xyz[match[1]] + match[2]) % 23] === match[3];
				char match1 = matcher.group(1).charAt(0);
				int match2 = Integer.parseInt(matcher.group(2));
				char match3 = matcher.group(3).charAt(0);
				return "TRWAGMYFPDXBNJZSQVHLCKE".charAt(xyz.get(match1) % 23 ) == match3;
			}
			return false;
		}

		private static boolean isCif(String str) {
			// let match: RegExpMatchArray | null = str.toUpperCase().match(/^[A-JUV](\d{7})([0-9])$/);
			Matcher matcher = Pattern.compile("^[A-JUV](\\d{7})([0-9])$").matcher(str.toUpperCase());
			if ( matcher.matches() ) {
				// return Nif.cifCtrlDigit(match[1]) === +match[2];
				return getCifCtrlDigit(matcher.group(1)) == Integer.parseInt(matcher.group(2));
			}
			matcher = Pattern.compile("^[N-SW](\\d{7})([A-J])$").matcher(str.toUpperCase());
			if ( matcher.matches() ) {
				// return 'JABCDEFGHI'[Nif.cifCtrlDigit(match[1])] === match[2];
				return "JABCDEFGHI".charAt(getCifCtrlDigit(matcher.group(1))) == matcher.group(2).charAt(0);
			}
			return false;
		}	
		
		private static boolean is(String str, String regex ) {
			Matcher matcher = Pattern.compile(regex).matcher(str.toUpperCase());
			if ( matcher.matches() ) {
		        // return 'TRWAGMYFPDXBNJZSQVHLCKE'[+match[1] % 23] === match[2];
				int match1 = Integer.parseInt(matcher.group(1));
				char match2 = matcher.group(2).charAt(0);
				return "TRWAGMYFPDXBNJZSQVHLCKE".charAt(match1 % 23 ) == match2;
			}
			return false;
		}
		
		private static int getCifCtrlDigit(String  str) {
			int digits [] = new int [str.length()];		
			for (int i = 0; i < str.length(); i++) {
				digits[i] = Integer.parseInt(String.valueOf(str.charAt(i)));
			}
			return getCifCtrlDigit(digits);
		}
		
		private static int getCifCtrlDigit(int  digits []) {
		    // const a = +digits[1] + +digits[3] + +digits[5];
			int a = digits[1] + digits[3] + digits[5];
			// const b1 = +digits[0] * 2;
			int b1 = digits[0] * 2;
			// const b3 = +digits[2] * 2;
			int b3 = digits[2] * 2;
			// const b5 = +digits[4] * 2;
			int b5 = digits[4] * 2;
			// const b7 = +digits[6] * 2;
			int b7 = digits[6] * 2;

			// const u1 = b1 % 10;
			int u1 = b1 % 10;
			// const d1 = (b1 - u1) / 10;
			int d1 = (b1 - u1) / 10;
			// const u3 = b3 % 10;
			int u3 = b3 % 10;
			// const d3 = (b3 - u3) / 10;
			int d3 = (b3 - u3) / 10;
			// const u5 = b5 % 10;
			int u5 = b5 % 10;
			// const d5 = (b5 - u5) / 10;
			int d5 = (b5 - u5) / 10;
			// const u7 = b7 % 10;
			int u7 = b7 % 10;
			// const d7 = (b7 - u7) / 10;
			int d7 = (b7 - u7) / 10;
			// const b = u1 + d1 + u3 + d3 + u5 + d5 + u7 + d7;
			int b = u1 + d1 + u3 + d3 + u5 + d5 + u7 + d7;

			// const c = a + b;
			int c = a + b;
			// const e = c % 10;
			int e = c % 10;

			// const d = e ? 10 - e : 0;
			int d = e != 0 ? 10 - e : 0;

			// return d;
			return d;
			
		}		
	}
	

	public AutoMLTemplate() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public InvoiceTemplate parse(String text, InvoiceBuilder<?> handler) throws IOException, UnknownInvoiceException {
		// 
		Collection<Nif> nifs = getNifs(text);
		Collection<Date> dates =  getDates(text);
		Collection<Double> amounts = getAmounts(text);
		
		return this;
	}
	

	private static Collection<Nif> getNifs(String text) {
		List<Nif> nifs = new ArrayList<Nif>();
		// const re: RegExp = /((ES[\s_-])?[A-Z0-9][\s_-]?[O0-9]{2}[_-]?[O0-9]{5}[_-]?[A-Z0-9]|[O0-9]{2}\s*[,]\s*[O0-9]{3}\s*[,]\s*[O0-9]{3}\s*[A-Z]|[A-Z0-9][\s_-]?([O0-9]\s?){7}[\s_-]?[A-Z0-9])/im;
		Pattern pattern = Pattern.compile("((ES[\\s_-])?[A-Z0-9][\\s_-]?[O0-9]{2}[_-]?[O0-9]{5}[_-]?[A-Z0-9]|[O0-9]{2}\\s*[,]\\s*[O0-9]{3}\\s*[,]\\s*[O0-9]{3}\\s*[A-Z]|[A-Z0-9][\\s_-]?([O0-9]\\s?){7}[\\s_-]?[A-Z0-9])", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		int index = 0;
		while ( matcher.find(index) ) {
			String str = matcher.group();
			// const match0: string = match[0]
			//       .replace(/O/, '0')
			//       .replace(/[,\s_-]/g, '')
			//       .replace(/^ES/, '');
			
			str = str
					.replaceAll("O", "0")
					.replaceAll("[,\\s_-]", "")
					.replaceAll("^ES", "");
			// let nif: Nif | null = Nif.parse(match0);
			Nif nif = Nif.parse(str);
			if ( nif != null ) {
				// const i: number = index + match.index!;
				int i = matcher.start();
				// let fake: boolean = i >= 4 && str.substr(i - 4, 4).match(/[A-Z0-9]{4}/i) !== null;
				boolean fake = i >= 4 && substr(text,i-4, 4).toUpperCase().matches("[A-Z0-9]{4}"); 
				if ( !fake ) {
					// fake = str.substr(i + match[0].length, 4).match(/[A-Z0-9]{4}/i) !== null;
					fake = substr(text,matcher.end() + 1, 4).toUpperCase().matches("[A-Z0-9]{4}");
				}
				if ( !fake ) {
					nifs.add(nif); 
			        // index += match.index! + match[0].length;
					index = matcher.end() + 1 ;
				}
				else {
					// index += match.index! + 1;
					index = matcher.start() + 1 ;
				}
			} else {
				// index += match.index! + 1;
				index = matcher.start() + 1 ;
			}
			
		}
		
		return nifs;
	}
	


	private static Collection<Date> getDates(String text) {
		List<Date> dates = new ArrayList<Date>();
	
		// let re: RegExp = /(0?[1-9]|[12][0-9]|3[01])\s*[-/]\s*(0?[1-9]|1[012])\s*[-/]\s*(20\d{2}|([12][0-9]))/gim;
		Pattern pattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])\\s*[-/]\\s*(0?[1-9]|1[012])\\s*[-/]\\s*(20\\d{2}|([12][0-9]))", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		Collection<String[]> found = findGroups(text, pattern);
		for ( String strs [] : found ) {
			// const day: number = parseInt(match[1], 10);
			// const month: number = parseInt(match[2], 10) - 1;
			// const year: number = match[4] ? parseInt(`20${match[4]}`, 10) : parseInt(match[3], 10);
			
			int day = Integer.parseInt(strs[1]);
			int month = Integer.parseInt(strs[2]) - 1 ;
			int year = strs[4] != null ? Integer.parseInt("20"+strs[4]) : Integer.parseInt(strs[3]);
			
			Date date = getDate(day, month, year);

			dates.add(date);
		}
		
	    Map<String,Integer> months = new HashMap<String,Integer>() {
	    	{
	    	      put("enero", 0);
	    	      put("en", 0);
	    	      put("febrero", 1);
	    	      put("feb", 1);
	    	      put("marzo", 2);
	    	      put("mar", 2);
	    	      put("abril", 3);
	    	      put("abr", 3);
	    	      put("mayo", 4);
	    	      put("may", 4);
	    	      put("junio", 5);
	    	      put("jun", 5);
	    	      put("julio", 6);
	    	      put("jul", 6);
	    	      put("agosto", 7);
	    	      put("ag", 7);
	    	      put("agto", 7);
	    	      put("septiembre", 8);
	    	      put("sep", 8);
	    	      put("sept", 8);
	    	      put("octubre", 9);
	    	      put("oct", 9);
	    	      put("noviembre", 10);
	    	      put("nov", 10);
	    	      put("diciembre", 11);
	    	      put("dic", 11);
	    	}
	    };		
		
		// re = /(0?[1-9]|[12][0-9]|3[01])\s*(?:de|\/)\s*([a-z]+\.?)\s*(?:de|\/)\s*(20\d{2}|([12][0-9]))/gim;
		pattern = Pattern.compile("(0?[1-9]|[12][0-9]|3[01])\\s*(?:de|\\/)\\s*([a-z]+\\.?)\\s*(?:de|\\/)\\s*(20\\d{2}|([12][0-9]))", Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		found = findGroups(text, pattern);
		for ( String strs [] : found ) {
			// const day: number = parseInt(match[1], 10);
			// const month: number = months[match[2].toLowerCase()];
			// const year: number = match[4] ? parseInt(`20${match[4]}`, 10) : parseInt(match[3], 10);
			
			int day = Integer.parseInt(strs[1]);
			int month = months.get(strs[2].toLowerCase());
			int year = strs[4] != null ? Integer.parseInt("20"+strs[4]) : Integer.parseInt(strs[3]);
			
			Date date = getDate(day, month, year);

			dates.add(date);
		}
		
		return dates;
	}


	private static Collection<Double> getAmounts(String text) {
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

	private static Date getDate(int day, int month, int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,0);
		calendar.set(Calendar.MINUTE,0);
		calendar.set(Calendar.SECOND,0);
		calendar.set(Calendar.MILLISECOND,0);

		calendar.set(Calendar.DAY_OF_MONTH,day);
		calendar.set(Calendar.MONTH,month);
		calendar.set(Calendar.YEAR,year);
		
		return calendar.getTime();
	}
	
		
	private static Collection<String> find(String text, Pattern pattern ) {
		List<String> found = new ArrayList<String>();
		
		Matcher matcher = pattern.matcher(text);
		while ( matcher.find() ) {
			found.add(matcher.group());
		}
		
		return found;
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
	
	private static String substr(String str, int start, int length) {
		int beginIndex = Math.max(start, 0);
		int endIndex = Math.min(str.length()+1, start+length);
		return str.substring(beginIndex, endIndex);
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
	
	
	public static void main(String[] args) {
		
		String text = "Pagado \n" + 
				"Vendido por Amazon EU S.à r.l., Sucursal en España \n" + 
				"IVA ESW0184081H\n" + 
				"Fecha de envío 10 de enero de 2019\n" + 
				"Número del documento AEU-SIM-INV-ES-2019-2524414\n" + 
				"ALEJANDRA JIMENEZ GONZÁLEZ \n" + 
				"Total a pagar 14,00 ?\n" + 
				"C/SANTÍSIMA TRINIDAD, 30, PLANTA 7,PUERTA 8 \n" + 
				"MADRID, MADRID, 28010 \n" + 
				"ES\n" + 
				"Si tienes preguntas sobre tus pedidos, visita https://www.amazon.es/contacto\n" + 
				"Dirección de facturación Dirección de envío Vendido por \n" + 
				"Alejandra Jimenez González Jonatan García Bellés Amazon EU S.à r.l., Sucursal en España \n" + 
				"C/Santísima Trinidad, 30, planta 7,puerta 8 C/Donantes de Sangre 1 7ºd Calle de Ramírez de Prado 5 \n" + 
				"Madrid, Madrid, 28010 Madrid, Madrid, 28041 28045 Madrid \n" + 
				"ES ES España \n" + 
				"IVA ESW0184081H \n" + 
				"Información del pedido\n" + 
				"Fecha del pedido 10 enero 2019\n" + 
				"Número del pedido 402-0296095-1463555\n" + 
				"Detalles del documento\n" + 
				"Descripción Cant. P. Unitario IVA % P. Unitario Precio total \n" + 
				"(IVA excluido) (IVA incluido) (IVA incluido)\n" + 
				"Agenda 2019 semana vista apaisada español 1 11,57 ? 21% 14,00 ? 14,00 ?\n" + 
				"ASIN: B07HJ8B5V1\n" + 
				"Total 14,00 ?\n" + 
				"IVA % Precio total IVA\n" + 
				"(IVA excluido)\n" + 
				"21% 11,57 ? 2,43 ?\n" + 
				"Total 11,57 ? 2,43 ?\n" + 
				"Nº Registro Integrado Industrial: 3725 (AEE) / 990 (Pilas y Acumuladores) \n" + 
				"LU-BIO-04 \n" + 
				"Amazon EU S.à r.l. - 38 avenue John F. Kennedy, L-1855 Luxemburgo \n" + 
				"R.C.S. Luxemburgo: B 101818 \n" + 
				"Amazon EU S.à r.l., Sucursal en España ? Calle de Ramírez de Prado 5, 28045 Madrid, España \n" + 
				"Registro Mercantil de Madrid ? Tomo 33.166, Libro 0, Folio 105, Seccion 8, Hoja M-596.819 ? NIF W0184081H\n" + 
				"Página 1 de 1\n" + 
				"\n" + 
				"";
		for ( Double amount : getAmounts(text) ) {
			System.out.println(amount);
		}
		for ( Date date : getDates(text) ) {
			System.out.println(date);
		}
		for ( Nif nif : getNifs(text) ) {
			System.out.println(nif.str);
		}
	}


}
