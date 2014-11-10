package com.esferalia.aon.shared.commons;

public class AonCharUtils {

	/**
	 * {@code \u000a} linefeed LF ('\n').
	 *
	 * @see <a
	 *      href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">JLF:
	 *      Escape Sequences for Character and String Literals</a>
	 * @since 2.2
	 */
	public static final char LF = '\n';
	/**
	 * {@code \u000d} carriage return CR ('\r').
	 *
	 * @see <a
	 *      href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-3.html#jls-3.10.6">JLF:
	 *      Escape Sequences for Character and String Literals</a>
	 * @since 2.2
	 */
	public static final char CR = '\r';

	/**
	 * <p>
	 * Checks whether the character is ASCII 7 bit printable.
	 * </p>
	 *
	 * <pre>
	 * CharUtils.isAsciiPrintable('a') = true
	 * CharUtils.isAsciiPrintable('A') = true
	 * CharUtils.isAsciiPrintable('3') = true
	 * CharUtils.isAsciiPrintable('-') = true
	 * CharUtils.isAsciiPrintable('\n') = false
	 * CharUtils.isAsciiPrintable('&copy;') = false
	 * </pre>
	 *
	 * @param ch
	 *            the character to check
	 * @return true if between 32 and 126 inclusive
	 */
	public static boolean isAsciiPrintable(final char ch) {
		return ch >= 32 && ch < 127;
	}
}
