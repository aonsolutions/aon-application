package solutions.aon.in.invoice.templates;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferenceParser {
	private static HashMap<String, String[]>  PATTERNS = new HashMap<String, String[]>();
	static {
		PATTERNS.put("B95868188", new String[]{"\\b(?<ref>PR\\d{10})\\b"});
		PATTERNS.put("W0013547E", new String[]{"\\bFactura\\s+(?<ref>[a-zA-Z_0-9]+)\\b"});
		PATTERNS.put("B87539284", new String[]{"\\b(?<ref>ZB\\d\\d\\-\\d{9})\\b"});
		PATTERNS.put("A63422141", new String[]{"\\b(?<ref>C202\\d{12})\\b"});
		PATTERNS.put("A39000013", new String[]{"\\b(?<ref>F202\\w{11})\\b"});
		PATTERNS.put("B82435074", new String[]{"\\b(?<ref>C/\\d{6})\\b"});
		PATTERNS.put("A86969607", new String[]{"\\b(?<ref>CI\\d{10}\\-\\d{4})\\b"});
		PATTERNS.put("B66941873", new String[]{"\\b(?<ref>CENIT/\\d{6})\\b"});
	}
	
	public static String getReference(String document, String text) {
		String[] patterns = PATTERNS.get(document);
		if (patterns != null) {
			for (String pat : patterns) {
				Pattern pattern = Pattern.compile( pat , Pattern.MULTILINE|Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(text);
				int index = 0;
				while (index <= text.length() && matcher.find(index) ) {
					String reference = matcher.group("ref");;
					return reference;
				}
			}
		}
		return null;
	}

	
	public static void main(String[] args) {
//		String text = "MADRID, 01/10/2020 Factura FBF71670 Objeto : Renting Contrato : A1A41781";
		String text = "Factura C202000000327308 - 09/10/2020 Nombre: AON SOLUTIONS SL Julio GARCIA Dirección: DUQUE DE WELLINGTON 52";
		System.out.println(  ReferenceParser.getReference("A63422141", text) );		
	}
}
