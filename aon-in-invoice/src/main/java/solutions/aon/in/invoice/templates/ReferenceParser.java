package solutions.aon.in.invoice.templates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferenceParser {
	
	public static String getReference(String[] patterns, String text) {
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
		String text = "( Álava )\nNº Factura: 202002350 Fecha: 19";
		String[] pat = new String[]{"\\bFactura:\\s+(?<ref>202\\d{6})\\b"};
		System.out.println( getReference(pat, text));		
	}
}
