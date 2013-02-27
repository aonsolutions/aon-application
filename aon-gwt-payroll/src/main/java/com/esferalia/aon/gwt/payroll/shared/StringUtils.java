package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.google.gwt.i18n.shared.DateTimeFormat;


public class StringUtils {
	
	
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static String capitalize(String str) {
		return capitalize(str, null);
	}


	public static String capitalize(String str, String delimiters) {
		if ( isEmpty(str) ){
			return str;
		}
		
		int strLen = str.length();
		StringBuffer buffer = new StringBuffer();
		 boolean capitalizeNext = true;
		for (int i = 0; i < strLen; i++) {
			char ch = str.charAt(i);
			if ( isDelimiter(ch, delimiters) ){
				buffer.append(ch);
				capitalizeNext = true;
			} else if (capitalizeNext) {
				buffer.append(Character.toUpperCase(ch));
				capitalizeNext = false;
			} else {
				buffer.append(ch);
			}
		}

		return buffer.toString();
	}
	
	public static String capitalizeFully(String str, String delimiters) {
		if ( isEmpty(str) ){
			return str;
		}
		str = str.toLowerCase();
		return capitalize(str, delimiters);
	}
	
	private static boolean isDelimiter(char ch, String delimiters) {
		return delimiters != null ? ( delimiters.indexOf(ch) != -1 ) : false;
	}
	

	public static String format(final String format, final String... args) {
	    String[] split = format.split("%s");
	    final StringBuffer msg = new StringBuffer();
	    for (int pos = 0; pos < split.length - 1; pos += 1) {
	        msg.append(split[pos]);
	        msg.append(args[pos]);
	    }
	    msg.append(split[split.length - 1]);
	    return msg.toString();
	 }
	
	
	public static boolean equals(String s1, String s2) {
		if ( s1 == s2 )
			return true;
		if ( s1 != null )
			return s1.equals(s2);
		return s2.equals(s2);
	}
	
	public static boolean equalsIgnoreCase(String s1, String s2) {
		if ( s1 == s2 )
			return true;
		if ( s1 != null )
			return s1.equals(s2);
		return s2.equalsIgnoreCase(s1);
	}
	
	public static String repeat(String str, int times){
		StringBuffer buffer = new StringBuffer();
		for ( int i= 0; i < times ; i++ )
			buffer.append(str);
		return str.toString();
	}		
}
