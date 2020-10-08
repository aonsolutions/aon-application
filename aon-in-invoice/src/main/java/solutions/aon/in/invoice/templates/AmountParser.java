package solutions.aon.in.invoice.templates;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esferalia.aon.watson.util.AonStringUtils;

public class AmountParser {
	
	public static List<Double> getAmounts(String text) {
		Locale ES = new Locale("es");
		String ds = Character.toString(DecimalFormatSymbols.getInstance( ES ).getDecimalSeparator());
		String gs = Character.toString(DecimalFormatSymbols.getInstance( ES ).getGroupingSeparator());
		
		// System.out.println( "Using '"+ds+"' as Decimal Separator and '"+gs+"' as Grouping Separator");
		
		String pat = "(?<integ>-?\\+?(\\d+\\"+gs+")*\\d+)"
				+"\\"+ds
				+"(?<fract>\\d+)"
				+"\\b"; 
		System.out.println( pat );
		
		Pattern pattern = Pattern.compile( pat , Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
		
		List<Double> amounts = new ArrayList<Double>();
		
		Matcher matcher = pattern.matcher(text);
		int index = 0;
		while ( index <= text.length() && matcher.find(index) ) {
			String integ = matcher.group("integ");
			integ = integ.replaceAll("O", "0")
						 .replaceAll( "\\" + gs , "");
			String fract = matcher.group("fract");
			System.out.print( 
				"("+ matcher.start() +", "+ matcher.end() +") Parte Entera ..: {" + integ + "}" + "  Parte Decimal .: {" + fract + "} --- ["
				+ AonStringUtils.substring( text, matcher.start(), matcher.end()) + "]" 
			); 
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
				System.out.print( fake ?" (FAKE 1!)":"" );
				if ( !fake ) {
					if ( (text.length() - matcher.end()) > 0 )  {
						String suffix = substr(text,matcher.end(), 1).toUpperCase();
						fake = suffix.matches("[-\\"+gs+"\\+\\"+ds+"0-9]") || suffix.matches("[\\w]");
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
		
	private static String substr(String str, int start, int length) {
		int beginIndex = Math.max(start, 0);
		int endIndex = Math.min(str.length()+1, start+length);
		return str.substring(beginIndex, endIndex);
	}

	public static void main(String[] args) {
		String text = 
			"FacturaRef. : A/11215Fecha facturación : 01/01/2019"
			+"Fecha de vencimiento : 01/01/2019"
			+"Código cliente : 430270"
			+"Ref. contrato : CT1601-0103 / 01/01/2016"
			+"Emisor: Enviar a:"
			+"AYSER - Desarrollos Informáticos, S.L. UDAPA S.Coop."
			+" Postas, 1 - ático izquierda. ARRIURDINA, 6 - POL. IND. JUNDIZ"
			+" 01001 Vitoria-Gasteiz 01015 VITORIA"
			+" Álava Álava"
			+" CIF/NIF: F01131978"
			+"  Teléfono: 945132358"
			+"  Correo: ayser@ayser.com"
			+"  Web: www.ayser.com"
			+"  Importes visualizados en Euros"
			+"  Descripción IVA P.U. Cant. Base imponible"
						+"  365_EMP_ESSENTIALS - O365BsnessEssentials ShrdSvr SNGL SubsVL 21% 71,76 1 71,76"			//71.76 71.76
			+"  OLP NL Annual Qlfd"
			+"  (De 14/02/2019 a 14/02/2020)"
			+"  Condiciones de pago: Pago a la entrega Base imponible 71,76"											//71.76
			+"  Total IVA 21% 15,07"																					//15.07
			+"  Forma de pago: Domiciliación Total 86,83"																//86.83
			+"  Sociedad Limitada - CIF/NIF: B01304419 - Núm. seguridad social: 01/1019918-39"
			+"  CNAE: 6203 - CIF intra.: ES-B01304419 1/1"
			+"  Powered by TCPDF (www.tcpdf.org)"
;
		Collection<Double> amounts = AmountParser.getAmounts( text );
		
		System.out.println( "Amounts..:" );
		for (Double amount : amounts) {
			System.out.println( amount );
		}
		System.out.println( "END" );
	}
}
