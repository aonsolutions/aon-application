package com.code.aon.webmail.bean;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AonMessageUtils {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AonMessageUtils.class);

	private static final long KB_BYTES = 1024;
	
	private static final long MB_BYTES = 1024 * KB_BYTES;
	
	private static final DecimalFormat KB_FORMAT = new DecimalFormat("0K");
	
	private static final DecimalFormat MB_FORMAT = new DecimalFormat("0.##MB");
	
	public static final String EMAIL_SEPARATOR = ",";
	
	// HTML line break, need for display of message
	public static final String HTML_LINE_BREAK = "<br/>";

	// search patterns, used for stripping html body tags from content
	private static final Pattern BODY_PATTERN = Pattern.compile(
			"<\\s*body[^>]*>(.*)<\\s*/\\s*body\\s*>", Pattern.CASE_INSENSITIVE
					+ Pattern.DOTALL);

	// search patterns, used for stripping html body tags from content
	private static final Pattern HTML_PATTERN = Pattern.compile(
			"<\\s*html[^>]*>(.*)<\\s*/\\s*html\\s*>", Pattern.CASE_INSENSITIVE
					+ Pattern.DOTALL);
	
	private static final Pattern UNDO_CID_PATTERN = Pattern.compile(
			"[\"\'](http://[^\"\']+.cid)[\"\']", Pattern.CASE_INSENSITIVE );	

	private static final Pattern ENCODED_PATTERN = Pattern.compile(
			"=\\?[^\\?]+\\?[BQ]\\?([^\\?]+)\\?=" );
	
	/**
	 * Utility method to extract content between the body tags of an HTML
	 * message.
	 * 
	 * @param content
	 *            content
	 * @return message content between html tags.
	 */
	public static String extractBodyInnerHTML(String content) {
		String match = null;
		try {
			Matcher bodyPatternMatcher = BODY_PATTERN.matcher(content);
			if (bodyPatternMatcher.find()) {
				match = bodyPatternMatcher.group(1);
			} else {
				return content;
			}
		} catch (IllegalStateException e) {
			return content;
		}
		return match;
	}

	/**
	 * Utility method to extract content between the content of an HTML
	 * message.
	 * 
	 * @param content
	 *            content
	 * @return message content between html tags.
	 */
	public static String extractInnerHTML(String content) {
		String match = null;
		try {
			Matcher htmlPatternMatcher = HTML_PATTERN.matcher(content);
			if (htmlPatternMatcher.find()) {
				match = htmlPatternMatcher.group(1);
			} else {
				return content;
			}
		} catch (IllegalStateException e) {
			return content;
		}
		return match;
	}

    public static String unparse_cid(String content){
		Matcher tagMatcher = UNDO_CID_PATTERN.matcher(content);
		if ( tagMatcher.find() ) {
			StringBuffer sb = new StringBuffer(content);
			int offset = 0;
			do {
				String cidURL = tagMatcher.group(1);
				int pos = cidURL.lastIndexOf('/');
				String newText = "cid:" + cidURL.substring(pos+1, cidURL.length()-4);
				int start = tagMatcher.start(1) + offset;
				int end = tagMatcher.end(1) + offset;
				sb.replace( start, end, newText);
				offset += newText.length() - cidURL.length();
			} while( tagMatcher.find() );
			return sb.toString();
		}		
		return content;
    }
    
    public static String decodeText(String content) {
    	if (! StringUtils.isEmpty(content) ) {
			Matcher tagMatcher = ENCODED_PATTERN.matcher(content);
			if ( tagMatcher.find() ) {
				StringBuffer sb = new StringBuffer(content);
				int offset = 0;
				do {
					String encodedText = tagMatcher.group();
					int start = tagMatcher.start() + offset;
					int end = tagMatcher.end() + offset;
					String newText = null;
					try {
						newText = MimeUtility.decodeWord(encodedText);
					} catch (Throwable e) {
						LOGGER.debug( "Error decoding text", e );
						newText = tagMatcher.group(1);
					}
					if (! StringUtils.isEmpty(newText) ) {
						sb.replace( start, end, newText);
						offset += newText.length() - encodedText.length();					
					}
				} while( tagMatcher.find() );
				return sb.toString();
			}
    	}
		return content;
    }	    

	public static String parse_cr(String data) {
		String newData = StringUtils.replace( data, "\r", "<br>" );
		newData = StringUtils.remove( newData, '\n' );
		return newData;
	}

	public static String parse_tags(String data) {
		String newData = StringUtils.replace( data, "<", "&lt;" );
		newData = StringUtils.replace( newData, ">", "&gt;" );
		return newData;
	}
	
	public static String parse_email(String data) {
		return data.replaceAll(EMAIL_SEPARATOR, "");
	}
	
	public static String getDisplaySize( long value ) {
		String result = "";
		double size = value;
		if ( size != -1 ) {
			if ( size > MB_BYTES ) {
				result = MB_FORMAT.format(size / MB_BYTES);
			} else {
				size = size < KB_BYTES ? 1 : size / KB_BYTES;
				result = KB_FORMAT.format(size);
			}
		}
		return result;		
	}
	
	public static boolean isValidEmail( String email ) {
		if (! StringUtils.isBlank(email) ) {
			try {
				new InternetAddress(email);
				return hasNameAndDomain(email);
		    } catch (AddressException ex){
		    	return false;
		    }			
		}
	    return false;
	}
	
	private static boolean hasNameAndDomain(String email){
		String[] tokens = email.split("@");
		return tokens.length==2 && !StringUtils.isBlank(tokens[0]) && 
			!StringUtils.isBlank(tokens[1]);
	}
	
}
