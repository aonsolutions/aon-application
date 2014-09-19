package com.esferalia.aon.gwt.common.shared;

import java.util.Arrays;
import java.util.Collection;

public class StringUtils {

	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	/**
	 * <p>
	 * Checks if a String is whitespace, empty ("") or null.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.isBlank(null)      = true
	 * StringUtils.isBlank("")        = true
	 * StringUtils.isBlank(" ")       = true
	 * StringUtils.isBlank("trepi")     = false
	 * StringUtils.isBlank("  trepi  ") = false
	 * </pre>
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isSpace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static String capitalize(String str) {
		return capitalize(str, null);
	}

	public static String capitalize(String str, String delimiters) {
		if (isEmpty(str)) {
			return str;
		}

		int strLen = str.length();
		StringBuffer buffer = new StringBuffer();
		boolean capitalizeNext = true;
		for (int i = 0; i < strLen; i++) {
			char ch = str.charAt(i);
			if (isDelimiter(ch, delimiters)) {
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
		if (isEmpty(str)) {
			return str;
		}
		str = str.toLowerCase();
		return capitalize(str, delimiters);
	}

	private static boolean isDelimiter(char ch, String delimiters) {
		return delimiters != null ? (delimiters.indexOf(ch) != -1) : false;
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
		if (s1 == s2)
			return true;
		if (s1 != null)
			return s1.equals(s2);
		return s2.equals(s1);
	}

	public static boolean equalsIgnoreCase(String s1, String s2) {
		if (s1 == s2)
			return true;
		if (s1 == null)
			return false;
		if (s2 == null)
			return false;
		return s2.equalsIgnoreCase(s1);
	}

	public static String repeat(char ch, int times) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < times; i++)
			buffer.append(ch);
		return buffer.toString();
	}

	public static String repeat(String str, int times) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < times; i++)
			buffer.append(str);
		return buffer.toString();
	}

	public static String reduce(String sep, String... strs) {
		return reduce(Arrays.asList(strs), sep);
	}

	public static String reduce(String strs[], String sep) {
		return reduce(Arrays.asList(strs), sep);
	}

	public static String reduce(Collection<String> collection, String sep) {
		StringBuffer buffer = new StringBuffer();
		for (String string : collection) {
			if (buffer.length() > 0)
				buffer.append(sep);
			if (string == null)
				continue;

			buffer.append(string.trim());
		}
		return buffer.toString();
	}

	public static String leftPad(int n, int size, char padChar) {
		return leftPad(Integer.toString(n), size, padChar);
	}

	public static String leftPad(String str, int size, char padChar) {
		if (str == null) {
			return null;
		}
		int pads = size - str.length();
		if (pads <= 0) {
			return str; // returns original String when possible
		}
		return repeat(padChar, pads).concat(str);
	}

	public static String abbreviate(String str, int offset, int maxWidth) {
		if (str == null) {
			return null;
		}
		if (maxWidth < 4) {
			throw new IllegalArgumentException(
					"Minimum abbreviation width is 4");
		}
		if (str.length() <= maxWidth) {
			return str;
		}
		if (offset > str.length()) {
			offset = str.length();
		}
		if ((str.length() - offset) < (maxWidth - 3)) {
			offset = str.length() - (maxWidth - 3);
		}
		if (offset <= 4) {
			return str.substring(0, maxWidth - 3) + "...";
		}
		if (maxWidth < 7) {
			throw new IllegalArgumentException(
					"Minimum abbreviation width with offset is 7");
		}
		if ((offset + (maxWidth - 3)) < str.length()) {
			return "..." + abbreviate(str.substring(offset), maxWidth - 3);
		}
		return "..." + str.substring(str.length() - (maxWidth - 3));
	}

	public static String abbreviate(String str, int maxWidth) {
		return abbreviate(str, 0, maxWidth);
	}

	public static String replace(String str, int startIndex, int endIndex,
			String replacement) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(str.substring(0, startIndex));
		buffer.append(replacement);
		buffer.append(str.substring(endIndex));
		return null;
	}


}
