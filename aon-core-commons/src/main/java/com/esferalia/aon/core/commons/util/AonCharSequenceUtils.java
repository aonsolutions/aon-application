package com.esferalia.aon.core.commons.util;

public class AonCharSequenceUtils {

	private static final int NOT_FOUND = -1;

	/**
	 * Green implementation of regionMatches.
	 *
	 * @param cs
	 *            the {@code CharSequence} to be processed
	 * @param ignoreCase
	 *            whether or not to be case insensitive
	 * @param thisStart
	 *            the index to start on the {@code cs} CharSequence
	 * @param substring
	 *            the {@code CharSequence} to be looked for
	 * @param start
	 *            the index to start on the {@code substring} CharSequence
	 * @param length
	 *            character length of the region
	 * @return whether the region matched
	 */
	static boolean regionMatches(final CharSequence cs,
			final boolean ignoreCase, final int thisStart,
			final CharSequence substring, final int start, final int length) {
		if (cs instanceof String && substring instanceof String) {
			return ((String) cs).regionMatches(ignoreCase, thisStart,
					(String) substring, start, length);
		}
		int index1 = thisStart;
		int index2 = start;
		int tmpLen = length;
		while (tmpLen-- > 0) {
			final char c1 = cs.charAt(index1++);
			final char c2 = substring.charAt(index2++);
			if (c1 == c2) {
				continue;
			}
			if (!ignoreCase) {
				return false;
			}
			// The same check as in String.regionMatches():
			if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
					&& Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Finds the first index in the {@code CharSequence} that matches the
	 * specified character.
	 * </p>
	 *
	 * @param cs
	 *            the {@code CharSequence} to be processed, not null
	 * @param searchChar
	 *            the char to be searched for
	 * @param start
	 *            the start index, negative starts at the string start
	 * @return the index where the search char was found, -1 if not found
	 */
	static int indexOf(final CharSequence cs, final int searchChar, int start) {
		if (cs instanceof String) {
			return ((String) cs).indexOf(searchChar, start);
		}
		final int sz = cs.length();
		if (start < 0) {
			start = 0;
		}
		for (int i = start; i < sz; i++) {
			if (cs.charAt(i) == searchChar) {
				return i;
			}
		}
		return NOT_FOUND;
	}

	/**
	 * Used by the indexOf(CharSequence methods) as a green implementation of
	 * indexOf.
	 *
	 * @param cs
	 *            the {@code CharSequence} to be processed
	 * @param searchChar
	 *            the {@code CharSequence} to be searched for
	 * @param start
	 *            the start index
	 * @return the index where the search sequence was found
	 */
	static int indexOf(final CharSequence cs, final CharSequence searchChar,
			final int start) {
		return cs.toString().indexOf(searchChar.toString(), start);
	}

	/**
	 * <p>
	 * Finds the last index in the {@code CharSequence} that matches the
	 * specified character.
	 * </p>
	 *
	 * @param cs
	 *            the {@code CharSequence} to be processed
	 * @param searchChar
	 *            the char to be searched for
	 * @param start
	 *            the start index, negative returns -1, beyond length starts at
	 *            end
	 * @return the index where the search char was found, -1 if not found
	 */
	static int lastIndexOf(final CharSequence cs, final int searchChar,
			int start) {
		if (cs instanceof String) {
			return ((String) cs).lastIndexOf(searchChar, start);
		}
		final int sz = cs.length();
		if (start < 0) {
			return NOT_FOUND;
		}
		if (start >= sz) {
			start = sz - 1;
		}
		for (int i = start; i >= 0; --i) {
			if (cs.charAt(i) == searchChar) {
				return i;
			}
		}
		return NOT_FOUND;
	}

	/**
	 * Used by the lastIndexOf(CharSequence methods) as a green implementation
	 * of lastIndexOf
	 *
	 * @param cs
	 *            the {@code CharSequence} to be processed
	 * @param searchChar
	 *            the {@code CharSequence} to be searched for
	 * @param start
	 *            the start index
	 * @return the index where the search sequence was found
	 */
	static int lastIndexOf(final CharSequence cs,
			final CharSequence searchChar, final int start) {
		return cs.toString().lastIndexOf(searchChar.toString(), start);
	}

	/**
	 * Green implementation of toCharArray.
	 *
	 * @param cs
	 *            the {@code CharSequence} to be processed
	 * @return the resulting char array
	 */
	static char[] toCharArray(final CharSequence cs) {
		if (cs instanceof String) {
			return ((String) cs).toCharArray();
		}
		final int sz = cs.length();
		final char[] array = new char[cs.length()];
		for (int i = 0; i < sz; i++) {
			array[i] = cs.charAt(i);
		}
		return array;
	}
}
