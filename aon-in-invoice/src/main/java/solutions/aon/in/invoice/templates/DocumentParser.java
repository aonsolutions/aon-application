package solutions.aon.in.invoice.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentParser  {

	public static Collection<Document> getNifs(String text) {
		List<Document> nifs = new ArrayList<Document>();
		// const re: RegExp = /((ES[\s_-])?[A-Z0-9][\s_-]?[O0-9]{2}[_-]?[O0-9]{5}[_-]?[A-Z0-9]|[O0-9]{2}\s*[,]\s*[O0-9]{3}\s*[,]\s*[O0-9]{3}\s*[A-Z]|[A-Z0-9][\s_-]?([O0-9]\s?){7}[\s_-]?[A-Z0-9])/im;
		Pattern pattern = Pattern.compile(
			"("
			//  -------- LEGAL_PERSON_NIF PATTERN  
			// -------- (1) --> X00000000
				+"[A-JUV]"
				+"[\\s]*"
				+"[-_/]?"
				+"[\\s]*"
				+"[0-9]{2}"
				+"[-_/\\.]?"
				+"[0-9]{3}"
				+"[-_/\\.]?"
				+"[0-9]{3}"
			//  -------- LEGAL_PERSON_NIF PATTERN 
			// -------- (2) --> X0000000X
			+"|"
				+"[NPQRSW]"
				+"[\\s-_/]?"
				+"[0-9]{7}"
				+"[\\s-_/]?"
				+"([A-J])"
			//  -------- DNI PATTERN 
			// -------- (1) --> 00000000X
			+"|"
				+"[0-9]?"
				+"[0-9]"
				+"[\\s-_/\\.]?"
				+"[0-9]{3}"
				+"[\\s-_/\\.]?"
				+"[0-9]{3}"
				+"[\\s-_/]?"
				+"[A-Z]"
			//  -------- NIE PATTERN 
			// -------- (1) --> X0000000X
			+"|"
				+"[XYZ]"
				+"[\\s-_/]?"
				+"[0-9]{7}"
				+"[\\s-_/]?"
				+"[A-HJ-NP-TV-Z]"
			+")"
			, Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		Matcher matcher = pattern.matcher(text);
		int index = 0;
		while ( matcher.find(index) ) {
			String str = matcher.group();
			str = str
					.replaceAll("O", "0")
					.replaceAll("[,\\.\\s_\\/-]", "")
					.replaceAll("^ES", "");
			
			// Algunos DNIS vienen sin el cero inicial.
			while (str.length() < 9) {
				str = "0" + str;
			}
			
			Document nif = Document.parse(str);
			
// -----------------------------------
			if (nif != null) {
				int i = matcher.start();
				int preffixOffset =  i>=4?4:i;
				String prefix = substr(text,i-preffixOffset, preffixOffset).toUpperCase();
				boolean fake = i >= 4 && prefix.matches("[A-Z0-9]{"+preffixOffset+"}"); 
				if ( !fake ) {
					if ( (text.length() - matcher.end()) > 0 )  {
						String suffix = substr(text,matcher.end(), 1).toUpperCase();
						boolean match = suffix.matches("[\\s|.|,|:]");
						fake = !match;
					}
				}
				if ( !fake ) {
					fake = isFakeDocument(nif);
				}
				if ( !fake ) {
					nifs.add(nif); 
					index = matcher.end() + 1 ;
				} else {
					index = matcher.start() + 1 ;
				}
			} else {
				index = matcher.start() + 1 ;
			}
			index = index > text.length()?text.length():index;
		}
		
		return nifs;
	}

	private static String substr(String str, int start, int length) {
		int beginIndex = Math.max(start, 0);
		int endIndex = Math.min(str.length()+1, start+length);
		return str.substring(beginIndex, endIndex);
	}
	
	private static boolean isFakeDocument(Document nif) {
		boolean vlid = DocumentUtil.isValid(nif.getData()); 
		return !vlid;
	}

	private static class DocumentUtil {
		private static final char[] DNI_LETTERS = {'T','R','W','A','G','M','Y','F','P','D','X','B','N','J','Z','S','Q','V','H','L', 'C', 'K', 'E' };
		private static final char[] NIF_LETTERS = {'J','A','B','C','D','E','F','G','H','I'};
		
		private static boolean isValid(String value) {
			if (value == null || value.length() == 0) {
				return false;
			}
			char[] doc = value.toCharArray();
			if (doc.length == 9) {
				String first = new String(doc,0,1);
				if (first.matches("[0-9|K|L|M]")) {
					return isValidNIF(doc);
				}
				if (first.matches("[X|Y|Z]")) {
					return isValidNIE(doc);
				}
				return isValidCIF(doc);
			}
			return false;
		}
		
		private static boolean isValidNIE(char[] doc) {
			if (doc.length != 9) {
				return false;
			}
			doc[0] = (doc[0] == 'X') ? '0' : doc[0];
			doc[0] = (doc[0] == 'Y') ? '1' : doc[0];
			doc[0] = (doc[0] == 'Z') ? '2' : doc[0];
			String numbers = new String(doc, 0, 8);
			if (!isNumeric(numbers)) {
				return false;
			}
			return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
		}
		
		private static boolean isValidNIF(char[] doc) {
			if (doc.length != 9) {
				return false;
			}
			doc[0] = (doc[0] == 'K' || doc[0] == 'L' || doc[0] == 'M') ? '0' : doc[0];
			String numbers = new String(doc, 0, 8);
			if (!isNumeric(numbers)) {
				return false;
			}
			return (doc[8] == DNI_LETTERS[(Integer.parseInt(numbers) % 23)]);
		}

		private static boolean isValidCIF(char[] doc) {
			if (doc.length != 9) {
				return false;
			}
			String first = new String(doc,0,1);
			/*
				A 	Número	Sociedades anónimas
				B 	Número	Sociedades de responsabilidad limitada
				C 	Número	Sociedades colectivas
				D 	Número	Sociedades comanditarias
				E 	Número	Comunidades de bienes
				F 	Número	Sociedades cooperativas
				G 	Número	Asociaciones y Fundaciones
				H 	Número	Comunidades de propietarios en régimen de propiedad horizontal
				J 	Número	Sociedades civiles, con o sin personalidad jurídica
				N 	Letra	Entidades extranjeras
				P 	Letra	Corporaciones Locales
				Q 	Letra	Organismos públicos
				R 	Letra	Congregaciones e instituciones religiosas
				S 	Letra	Órganos de la Administración General del Estado y de las Comunidades Autónomas
				U 	Número	Uniones Temporales de Empresas
				V 	Número	Otros tipos no definidos en el resto de claves
				W 	Letra	Establecimientos permanentes de entidades no residentes en España 			 
			*/
			if (!first.matches("[A|B|C|D|E|F|G|H|J|N|P|Q|R|S|U|V|W]")) {
				return false;
			}
			int lInDC = 0;
			for (int i = 1; i < 8; ++i) {
				String strDigit = new String(doc, i, 1);
				if (!isNumeric(strDigit)) {
					return false;
				}
				int digit = Integer.parseInt(strDigit);
				if ((i % 2) != 0) {
					digit *= 2;
					if (digit >= 10) {
						digit -= 9;
					}
				}
				lInDC += digit;
			}
			// Buscamos el multiplo de diez mas cercano mayor al numero calculado.
			lInDC = (((lInDC / 10) + 1) * 10) - lInDC;
			if (lInDC == 10) {
				lInDC = 0;
			}
			if (first.matches("[P|N|S|Q|R|W]")) {
				return (NIF_LETTERS[lInDC] == doc[8]);
			}
			String strDC = new String(doc, 8, 1);
			if (!isNumeric(strDC)) {
				return false;
			}
			return (Integer.parseInt(strDC) == lInDC);
		}
		
		
		private static boolean isNumeric(String str) {
	        if (str == null) {
	            return false;
	        }
	        int sz = str.length();
	        for (int i = 0; i < sz; i++) {
	            if (Character.isDigit(str.charAt(i)) == false) {
	                return false;
	            }
	        }
	        return true;
	    }

	}

	public static void main(String[] args) {
		Collection<Document> documents = DocumentParser.getNifs(
				"asdfsd V01130111 :	eee" 
		);
		for (Document doc : documents) {
			System.out.println( doc.getType() + " --- " + doc.getData() );
		}
		System.out.println( "END" );
	}
	
}
