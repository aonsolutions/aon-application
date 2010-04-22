package com.code.aon.file.format;

/**
 * Alphabetic Format
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class Alphabetic implements Format {

	/**
	 * The pattern
	 */
	private Object pattern; // X(?)
	/**
	 * The size
	 */
	private int size;
	/**
	 * Is UpShift
	 */
	private boolean upShift = true;
	/**
	 * Default vlaue
	 */
	private static final String DEFAULT_VALUE = " ";

	public void applyPattern(Object newPattern) {
		this.applyPattern((String)newPattern);
	}

	/**
	 * Assigns new pattern
	 * 
	 * @param newPattern new pattern
	 */
	public void applyPattern(String newPattern) {
		this.pattern = newPattern.trim();
		size = this.getPrecision((String)this.pattern);
	}

	public String format(Object value) {
		return this.format((String)value);
	}

	/**
	 * Formats a String
	 * 
	 * @param value the String
	 * @return formatted String
	 */
	public String format(String value) {
		value = (value == null) ? DEFAULT_VALUE : value;

		String tmp = new String(value.trim());
		tmp = (tmp.length() > size) ? tmp.substring(0, size) : tmp;
		tmp = (upShift) ? tmp.toUpperCase() : tmp;

		return tmp + this.fillObject(this.size - tmp.length());
	}

	/**
	 * Fills the objects to the required size
	 * 
	 * @param size required size
	 * @return String filled
	 */
	private String fillObject(int size) {
		StringBuffer sb = new StringBuffer(size);
		for (int i=0; i<size; i++) {
			sb.append(BLANK);
		}
		return sb.toString();
	}

	/**
	 * Returns the precision of the pattern
	 * 
	 * @param pattern the pattern
	 * @return the precision
	 */
	public int getPrecision(String pattern) {
		int firstParen = pattern.indexOf(LBRAK);
		int lastParen = pattern.indexOf(RBRAK, firstParen);
		return (firstParen == -1 && lastParen == -1) ? pattern.length() : Integer.parseInt(pattern.substring(firstParen + 1, lastParen));
	}

	public Object parse(String value) throws java.text.ParseException {
		return value.trim();
	}

	static public void main(String[] args) {
		Alphabetic alpha = new Alphabetic();
		alpha.applyPattern("X(34)");
		System.out.println ("#"+alpha.format("Q2EQWWW")+"#");
	}

}