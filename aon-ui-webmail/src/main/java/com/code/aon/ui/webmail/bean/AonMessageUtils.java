package com.code.aon.ui.webmail.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AonMessageUtils {
	
	public static final String EMAIL_SEPARATOR = ",";
	
	// HTML line break, need for display of message
	public static final String HTML_LINE_BREAK = "<br/>";

	// &nbsp; , space attribute
	public static final String HTML_SPACE = "&nbsp;";

	// search patterns, used for stripping html body tags from content
	public static final Pattern BODY_PATTERN = Pattern.compile(
			"<\\s*body[^>]*>(.*)<\\s*/\\s*body\\s*>", Pattern.CASE_INSENSITIVE
					+ Pattern.DOTALL);

	// search pattern, common incountered eamil tags to remove
	public static final Pattern TAG_PATTERN = Pattern
			.compile(
					"</?\\w+((\\s+\\w+(\\s*=\\s*(?:\".*?\"|'.*?'|[^'\">\\s]+))?)+\\s*|\\s*)/?>",
					Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

	public static final Pattern TEXT_LINE_BREAK_PATTERN = Pattern.compile(
			"\n", Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

	public static final Pattern HTML_LINE_BREAK_PATTERN = Pattern.compile(
			HTML_LINE_BREAK, Pattern.CASE_INSENSITIVE + Pattern.DOTALL);


	/**
	 * Utility method to extract content between the body tags of an HTML
	 * message.
	 * 
	 * @param content
	 *            content
	 * @return message content between html tags.
	 */
	public static StringBuffer extractBodyInnerHTML(StringBuffer content) {
		StringBuffer match = new StringBuffer();
		try {
			Matcher bodyPatternMatcher = BODY_PATTERN.matcher(content);
			if (bodyPatternMatcher.find()) {
				match.append(bodyPatternMatcher.group(1));
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
	
	
	protected static final Pattern CID_PATTERN = Pattern.compile(
			"cid:[^\"\']+\"|\'", Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

    public static String parse_cid(String content){
		String textRplc = new String(content);
		Matcher tagMatcher = CID_PATTERN.matcher(content);
		while(tagMatcher.find()){
			String text = tagMatcher.group();
			String newText = new String(text);
			newText = newText.replace("cid:", "");
			newText = newText.replace("\"", ".cid\"");
			textRplc = textRplc.replace(text, newText);
		}
		return textRplc;
    }

	protected static final Pattern UNDO_CID_PATTERN = Pattern.compile(
			"[\"|\'][^\"\']+.cid", Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

    public static String unparse_cid(String content){
		String textRplc = new String(content);
		Matcher tagMatcher = UNDO_CID_PATTERN.matcher(content);
		while(tagMatcher.find()){
			String text = tagMatcher.group();
			String newText = new String(text);
			newText = newText.replace(".cid", "");
			newText = newText.replace("\"", "\"cid:");
			textRplc = textRplc.replace(text, newText);
		}
		return textRplc;
    }

    public static List getAllCid(String content){
    	List cids = new ArrayList();
		Matcher tagMatcher = UNDO_CID_PATTERN.matcher(content);
		while(tagMatcher.find()){
			String text = tagMatcher.group();
			String newText = new String(text);
			newText = newText.replace(".cid", "");
			newText = newText.replace("\"", "");
			cids.add(newText);
		}
		return cids;
    }

	public static String parse_cr(String data) {
		int currentIndex = 0;
		currentIndex = data.indexOf(13);
		while (currentIndex != -1) {
			data = data.substring(0, currentIndex) + "<br>"
					+ data.substring(currentIndex + 1, data.length());
			currentIndex = data.indexOf(13, currentIndex);
		}
		currentIndex = 0;
		currentIndex = data.indexOf(10);
		while (currentIndex != -1) {
			data = data.substring(0, currentIndex)
					+ data.substring(currentIndex + 1, data.length());
			currentIndex = data.indexOf(10, currentIndex);
		}
		return data;
	}

	public static String parse_tags(String data) {
		int currentIndex = 0;
		currentIndex = data.indexOf("<");
		while (currentIndex != -1) {
			data = data.substring(0, currentIndex) + "&lt;"
					+ data.substring(currentIndex + 1, data.length());
			currentIndex = data.indexOf("<", currentIndex);
		}
		currentIndex = 0;
		currentIndex = data.indexOf(">");
		while (currentIndex != -1) {
			data = data.substring(0, currentIndex) + "&gt;"
					+ data.substring(currentIndex + 1, data.length());
			currentIndex = data.indexOf(">", currentIndex);
		}
		return data;
	}
	
	public static String parse_email(String data) {
		return data.replaceAll(EMAIL_SEPARATOR, "");
	}
	
}
