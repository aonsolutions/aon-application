package com.esferalia.aon.gwt.employee.shared;


public class StringUtils {
	
	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static String capitalize(String str, String delimiters) {
		if ( isEmpty(str) || isEmpty(delimiters)){
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
		if ( isEmpty(str) || isEmpty(delimiters)){
			return str;
		}
		str = str.toLowerCase();
		return capitalize(str, delimiters);
	}
	
	private static boolean isDelimiter(char ch, String delimiters) {
		return delimiters.indexOf(ch) != -1;
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
	
}
