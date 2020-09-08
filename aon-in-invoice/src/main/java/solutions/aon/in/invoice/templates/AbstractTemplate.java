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

import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.in.invoice.InvoiceTemplate;
import solutions.aon.in.invoice.UnknownInvoiceException;

public abstract class AbstractTemplate implements InvoiceTemplate {

	protected static String string(Matcher matcher, String name ) {
		String group = matcher.group(name);
		group = AonStringUtils.trim(group);
		return group;
	}
	
	protected static Locale.IsoCountryCode country(Matcher matcher, String name ) {
		String group = matcher.group(name);
		group = AonStringUtils.trim(group);
		return Locale.IsoCountryCode.valueOf(group);
	}

	protected static Date date(Matcher matcher, String name, String pattern  ) throws UnknownInvoiceException{
		String group = matcher.group(name);
		group = AonStringUtils.trim(group);
		try {
			return new SimpleDateFormat(pattern,  new Locale("es", "ES")).parse(group);
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
}
