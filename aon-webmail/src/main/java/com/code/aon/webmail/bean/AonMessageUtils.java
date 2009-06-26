package com.code.aon.webmail.bean;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang.StringUtils;

public class AonMessageUtils {

	private static final long KB_BYTES = 1024;
	
	private static final long MB_BYTES = 1024 * KB_BYTES;
	
	private static final DecimalFormat KB_FORMAT = new DecimalFormat("0K");
	
	private static final DecimalFormat MB_FORMAT = new DecimalFormat("0.##MB");
	
	public static final String EMAIL_SEPARATOR = ",";
	
	// HTML line break, need for display of message
	public static final String HTML_LINE_BREAK = "<br/>";

	// &nbsp; , space attribute
	private static final String HTML_SPACE = "&nbsp;";

	// search patterns, used for stripping html body tags from content
	private static final Pattern BODY_PATTERN = Pattern.compile(
			"<\\s*body[^>]*>(.*)<\\s*/\\s*body\\s*>", Pattern.CASE_INSENSITIVE
					+ Pattern.DOTALL);

	// search patterns, used for stripping html body tags from content
	private static final Pattern HTML_PATTERN = Pattern.compile(
			"<\\s*html[^>]*>(.*)<\\s*/\\s*html\\s*>", Pattern.CASE_INSENSITIVE
					+ Pattern.DOTALL);
	
	// search pattern, common incountered eamil tags to remove
	private static final Pattern TAG_PATTERN = Pattern
			.compile(
					"</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>",
					Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

	private static final Pattern TEXT_LINE_BREAK_PATTERN = Pattern.compile(
			"\n", Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

	private static final Pattern UNDO_CID_PATTERN = Pattern.compile(
			"[\"|\'][^\"\']+.cid", Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

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
	
	/**
	 * Called to remove html tags from content of a text/html message to view in
	 * FF.
	 * 
	 * @param content
	 * @return content stripped of html tags.
	 */
	public static StringBuffer removeHTMLTags(StringBuffer content) {
		StringBuffer match = new StringBuffer();
		try {
			Matcher tagMatcher = TAG_PATTERN.matcher(content);
			match.append(tagMatcher.replaceAll(HTML_SPACE));
		} catch (IllegalStateException e) {
			return content;
		}
		return match;
	}

	/**
	 * Called to remove all text line breaks and replace with html breaks.
	 * 
	 * @param content
	 * @return content stripped of html tags.
	 */
	public static StringBuffer removeTextChariageBreaks(String content) {
		StringBuffer match = new StringBuffer();
		try {
			Matcher tagMatcher = TEXT_LINE_BREAK_PATTERN.matcher(content);
			match.append(tagMatcher.replaceAll(HTML_LINE_BREAK));
		} catch (IllegalStateException e) {
			return new StringBuffer(content);
		}
		return match;
	}

    public static String unparse_cid(String content){
		String textRplc = new String(content);
		Matcher tagMatcher = UNDO_CID_PATTERN.matcher(content);
		while(tagMatcher.find()){
			String text = tagMatcher.group();
			String newText = new String(text);
			int pos = newText.lastIndexOf("/");
			newText = "\"cid:" + newText.substring(pos+1, newText.length()-4);
			textRplc = textRplc.replace(text, newText);
		}
		return textRplc;
    }

	public static String parse_cr(String data) {
		String newData = StringUtils.replace( data, "\r", "<br>" );
		newData = StringUtils.remove( newData, '\n' );
		return newData;
	}

	public static String parse_tags(String data) {
		String newData = StringUtils.replace( data, "<", "&lt;" );
		newData = StringUtils.replace( data, ">", "&gt;" );
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
				size = (size < KB_BYTES) ? 1 : size / KB_BYTES;
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
		return (tokens.length == 2) && (!StringUtils.isBlank(tokens[0])) && 
			(!StringUtils.isBlank(tokens[1]));
	}
	
}
