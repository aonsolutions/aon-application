package com.code.aon.file.format;

/**
 * Nif Format
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class Nif implements Format {

	/**
	 * The pattern
	 */
	private Object pattern; // X(?)
	/**
	 * The size
	 */
	private int size;
	/**
	 * Is upshift
	 */
	private boolean upShift = true;
	/**
	 * Zero String
	 */
	public static final String ZERO = "0";
	/**
	 * Default value String
	 */
	private static final String DEFAULT_VALUE = " ";

	public void applyPattern(Object newPattern) {
		this.applyPattern((String)newPattern);
	}

	/**
	 * Assigns a pattern
	 * 
	 * @param newPattern the string of the pattern
	 */
	public void applyPattern(String newPattern) {
		this.pattern = newPattern.trim();
		size = getPrecision((String)this.pattern);
	}

	public String format(Object value) {
		return this.format((String)value);
	}

	/**
	 * Formats the String
	 * 
	 * @param value the original string
	 * @return the string formatted
	 */
	public String format(String value) {
		value = (value == null) ? DEFAULT_VALUE : value;

		String tmp = new String(value.trim());
		tmp = (tmp.length() > size) ? tmp.substring(0, size) : tmp;
		tmp = (upShift) ? tmp.toUpperCase() : tmp;

		return (tmp.equals("")) ? (tmp + this.fillObject((this.size - tmp.length()), BLANK)) : (this.fillObject((this.size - tmp.length()), ZERO) + tmp);
	}

	/**
	 * Fills the objects to the required size with a filler
	 * 
	 * @param size required size
	 * @param filler the filler
	 * @return String filled
	 */
	private String fillObject(int size, String filler) {
		StringBuffer sb = new StringBuffer(size);
		for (int i=0; i<size; i++) {
			sb.append(filler);
		}
		return sb.toString();
	}

	/**
	 * Returns the precision
	 * 
	 * @param pattern the pattern to analyze
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
		Nif nif = new Nif();
		nif.applyPattern("X(9)");
		System.out.println ("#"+nif.format("34556K")+"#");
	}

}