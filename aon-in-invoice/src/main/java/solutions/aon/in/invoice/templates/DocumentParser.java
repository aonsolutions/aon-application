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
				+"[\\s-_/]?"
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
	
	
	public static void main(String[] args) {
		Collection<Document> documents = DocumentParser.getNifs(
//				"Pagado "
//				+"\nVendido por Amazon EU S.� r.l., Sucursal en Espa�a "
//				+"\nIVA ESW0184081H"
//				+"\nFecha de env�o 10 enero 2019"
//				+"\nN�mero del documento AEU-SIM-INV-ES-2019-2524414"
//				+"\nALEJANDRA JIMENEZ GONZ�LEZ "
//				+"\nTotal a pagar 14,00 ?"
//				+"\nC/SANT�SIMA TRINIDAD, 30, PLANTA 7,PUERTA 8 "
//				+"\nMADRID, MADRID, 28010 "
//				+"\nES"
//				+"\nSi tienes preguntas sobre tus pedidos, visita https://www.amazon.es/contacto"
//				+"\nDirecci�n de facturaci�n Direcci�n de env�o Vendido por "
//				+"\nAlejandra Jimenez Gonz�lez Jonatan Garc�a Bell�s Amazon EU S.� r.l., Sucursal en Espa�a "
//				+"\nC/Sant�sima Trinidad, 30, planta 7,puerta 8 C/Donantes de Sangre 1 7�d Calle de Ram�rez de Prado 5 "
//				+"\nMadrid, Madrid, 28010 Madrid, Madrid, 28041 28045 Madrid "
//				+"\nES ES Espa�a "
//				+"\nIVA ESW0184081H "
//				+"\nInformaci�n del pedido"
//				+"\nFecha del pedido 10 enero 2019"
//				+"\nN�mero del pedido 402-0296095-1463555"
//				+"\nDetalles del documento"
//				+"\nDescripci�n Cant. P. Unitario IVA % P. Unitario Precio total" 
//				+"\n(IVA excluido) (IVA incluido) (IVA incluido)"
//				+"\nAgenda 2019 semana vista apaisada espa�ol 1 11,57 ? 21% 14,00 ? 14,00 ?"
//				+"\nASIN: B07HJ8B5V1"
//				+"\nTotal 14,00 ?"
//				+"\nIVA % Precio total IVA"
//				+"\n(IVA excluido)"
//				+"\n21% 11,57 ? 2,43 ?"
//				+"\nTotal 11,57 ? 2,43 ?"
//				+"\nN� Registro Integrado Industrial: 3725 (AEE) / 990 (Pilas y Acumuladores)" 
//				+"\nLU-BIO-04 "
//				+"\nAmazon EU S.� r.l. - 38 avenue John F. Kennedy, L-1855 Luxemburgo" 
//				+"\nR.C.S. Luxemburgo: B 101818 "
//				+"\nAmazon EU S.� r.l., Sucursal en Espa�a ? Calle de Ram�rez de Prado 5, 28045 Madrid, Espa�a "
//				+"\nRegistro Mercantil de Madrid ? Tomo 33.166, Libro 0, Folio 105, Seccion 8, Hoja M-596.819 ? NIF W-0184081H"
//				+"\nP�gina 1 de 1"
//				
//				+"\n44671367P"
//				+"\n44671367-P"
//				+"\n44.671.367-P"
//				+"\n44671367/P"
//				+"\n44.671.367/P"
//				"7o8Cw6BtfN9l4hgFlfad197PQXNt25PCLvoE0Sqk 73742960S S0DCDY9dfLdi1V08i43q8KybGI0pNFU"
				"73742960S:	eee" 
		);
		for (Document doc : documents) {
			System.out.println( doc.getType() + " --- " + doc.getData() );
		}
		
		System.out.println( "END" );
		 
	}
}
