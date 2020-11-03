package solutions.aon.in.invoice.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.UnknownInvoiceException;

public abstract class AbstractTemplate implements InvoiceTemplate {

	protected static String string(Matcher matcher, String name ) {
		String group = matcher.group(name);
		return trim(group);
	}
	
	protected static Locale.IsoCountryCode country(Matcher matcher, String name ) {
		String group = matcher.group(name);
		return Locale.IsoCountryCode.valueOf(trim(group));
	}

	protected static Date date(Matcher matcher, String name, String pattern  ) throws UnknownInvoiceException{
		String group = matcher.group(name);
		try {
			return new SimpleDateFormat(pattern,  new Locale("es", "ES")).parse(trim(group));
		} catch (ParseException e) {
			throw new UnknownInvoiceException(e);
		}
	}
		
	protected static Matcher find( BufferedReader reader, Pattern ...patterns ) throws IOException, UnknownInvoiceException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			for ( Pattern pattern : patterns ) {
				Matcher matcher = pattern.matcher(line) ;
				if ( matcher.matches() ) {
					return matcher;
				}
			}
		}
		
		throw new UnknownInvoiceException(String.format("Pattern: '%s' Not found" ,  Arrays.stream(patterns).map(p -> p.pattern()).collect(Collectors.joining(","))));
				
	}
	
	private static String trim(final String str) {
		return str == null ? null : str.trim();
	}
	
}
