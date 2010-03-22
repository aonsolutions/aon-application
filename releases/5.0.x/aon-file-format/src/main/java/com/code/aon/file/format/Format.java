package com.code.aon.file.format;

/**
 * The Format interface for components
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public interface Format {

	/**
	 * The blank string
	 */
	public static final String BLANK = " ";
	/**
	 * The left braket string
	 */
	public static final String LBRAK = "(";
	/**
	 * The right braket string
	 */
	public static final String RBRAK = ")";

	/**
	 * Asigns a pattern
	 * 
	 * @param newPattern the pattern
	 */
	public void applyPattern(Object newPattern);

	/**
	 * Recovers the value formated in a string
	 * 
	 * @param value
	 * @return value formatted
	 */
	public String format(Object value);

	/**
	 * Parses a String
	 * 
	 * @param value the string
	 * @return the object parsed
	 * @throws java.text.ParseException
	 */
	public Object parse(String value) throws java.text.ParseException;

}