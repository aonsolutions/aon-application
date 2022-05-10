package com.code.aon.ui.help;

import static com.esferalia.aon.watson.util.AonStringUtils.INDEX_NOT_FOUND;
import static com.esferalia.aon.watson.util.AonStringUtils.indexOfIgnoreCase;
import static com.esferalia.aon.watson.util.AonStringUtils.isBlank;

import java.util.List;
import java.util.function.UnaryOperator;

import org.apache.commons.lang.StringEscapeUtils;

import com.esferalia.aon.watson.util.AonStringUtils;

public class HelpData {
	
	private String uri;
	private String title;
	
	public String getURI() {
		return uri;
	}
	
	public HelpData setURI(String uri) {
		this.uri = uri;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public HelpData setTitle(String title) {
		this.title = title;
		return this;
	}
	public String getDisplay(String filter) {
		
		return StringEscapeUtils.escapeHtml(title);
	}

	
	public String __getDisplay(String filter) {
		
		String htmlTitle = StringEscapeUtils.escapeHtml(title);

		
		if ( isBlank(filter) ) 
			return htmlTitle;
		
		StringBuilder display = new StringBuilder();

		try {
			display.append(replace(htmlTitle,filter, s -> "<b>"+s+"</b>"));
		} catch ( Exception e ) {
			try {
				List<String> matches = AonStringUtils.getMatching(title, filter);				
				String aux = title;
				
				for (String match : matches) {
					aux = replace(aux, match, p -> "<b>" + p + "</b>" ); // aux.replace(match , "<b>" + match + "</b>");
				}
				
				display.append(aux);
			} catch(Exception ex) {
				display.append(title);
			}
			
		}
		
		return display.toString();
	}
	
	public static String replace(final String text, final String searchString,
			UnaryOperator<String> replace) {

		int start = 0;
		int end = indexOfIgnoreCase(text, searchString, start);
		if (end == INDEX_NOT_FOUND) {
			throw new IndexOutOfBoundsException();
		}
		final int replLength = searchString.length();
		final StringBuilder buf = new StringBuilder();
		while (end != INDEX_NOT_FOUND) {			
			String replacement  = replace.apply(text.substring(end,end+replLength));
			buf.append(text.substring(start, end)).append(replacement);
			start = end + replLength;
			end = indexOfIgnoreCase(text,searchString, start);
			
			// Array overflow control
			if(buf.toString().length() > Integer.MAX_VALUE / 2) {
				break;
			}
		}
		buf.append(text.substring(start));
		return buf.toString();
	}
	
	

}
