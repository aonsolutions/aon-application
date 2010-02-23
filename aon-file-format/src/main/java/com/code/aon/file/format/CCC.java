package com.code.aon.file.format;

/**
 * CCC Format
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class CCC implements Format {

	/**
	 * The pattern
	 */
	private Object pattern;
	/**
	 * The size
	 */
	private int size;
	/**
	 * The ZERO string
	 */
	public static final String ZERO = "0";
	/**
	 * Default value
	 */
	private static final String DEFAULT_VALUE = "0";

	public void applyPattern(Object newPattern) {
		this.applyPattern((String)newPattern);
	}

	/**
	 * Assigns a pattern
	 * 
	 * @param newPattern
	 */
	public void applyPattern(String newPattern) {
		this.pattern = newPattern.trim();
		size = getPrecision((String)this.pattern);
	}

	public String format(Object value) {
		return this.format((String)value);
	}

	/**
	 * Formats a value string
	 * 
	 * @param value the value to be formatted
	 * @return value formattes
	 */
	public String format(String value) {
		value = (value == null) ? DEFAULT_VALUE : value;

		String tmp = new String(value.trim());
		tmp = tmp.replaceAll("\\.", "");
		tmp = (tmp.length() > size) ? tmp.substring(0, size) : tmp;
		tmp = this.fillObject((this.size - tmp.length()), ZERO) + tmp;
		return (this.formatAccordPattern((String)pattern, tmp));
	}

	/**
	 * Fills the object to a size
	 * 
	 * @param size the final size
	 * @param filler the filler string
	 * @return the string filled
	 */
	private String fillObject(int size, String filler) {
		StringBuffer sb = new StringBuffer(size);
		for (int i=0; i<size; i++) {
			sb.append(filler);
		}
		return sb.toString();
	}

	/**
	 * Format for a concrete pattern
	 * 
	 * @param pattern the pattern
	 * @param value the value to be formatted
	 * @return the value formatted
	 */
	public String formatAccordPattern(String pattern, String value) {
		StringBuffer sb = new StringBuffer(size);
		for (int i=0; i<pattern.length(); i++) {
			char ch = pattern.charAt(i);
			if (ch == 'E') {
				sb.append(value.substring(0,4));
			}
			if (ch == 'S') {
				sb.append(value.substring(4,8));
			}
			if (ch == 'D') {
				sb.append(value.substring(8,10));
			}
			if (ch == 'C') {
				sb.append(value.substring(10,20));
			}
		}

		return sb.toString();
	}

	/**
	 * Return the precision
	 * 
	 * @param pattern the pattern
	 * @return the precision
	 */
	public int getPrecision(String pattern) {
		return 20;
	}

	public Object parse(String value) throws java.text.ParseException {
		return value.trim();
	}

	static public void main(String[] args) {
		CCC ccc = new CCC();
		ccc.applyPattern("ESDC");
		System.out.println ("#"+ccc.format(null)+"#");
	}

}