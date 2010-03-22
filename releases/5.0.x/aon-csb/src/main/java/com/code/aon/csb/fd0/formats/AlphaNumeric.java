package com.code.aon.csb.fd0.formats;

/**
 * Alphanumeric Format
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class AlphaNumeric implements Format {

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
	 * Fill Type RIGTH or other
	 */
	private String fillSide = "RIGHT";
	/**
	 * Default value 
	 */
	private static final String DEFAULT_VALUE = " ";

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.formats.Format#applyPattern(java.lang.Object)
	 */
	public void applyPattern(Object newPattern) {
		this.applyPattern((String)newPattern);
	}

	/**
	 * Assigns this pattern
	 * 
	 * @param newPattern the pattern
	 */
	public void applyPattern(String newPattern) {
		this.pattern = newPattern.trim();
		size = getPrecision((String)this.pattern);
	}

	/**
	 * Assign fill side
	 * 
	 * @param fillSide the fill side
	 */
	public void setFillSide(String fillSide) {
		this.fillSide = fillSide;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.formats.Format#format(java.lang.Object)
	 */
	public String format(Object value) {
		return this.format((String)value);
	}

	/**
	 * Return the String formatted
	 * 
	 * @param value String to format
	 * @return string formatted
	 */
	public String format(String value) {
		value = (value == null) ? DEFAULT_VALUE : value;

		String tmp = new String(value);
		tmp = (tmp.length() > size) ? tmp.substring(0, size) : tmp;
		tmp = (upShift) ? tmp.toUpperCase() : tmp;
		String fill = this.fillObject(this.size - tmp.length());

		return fillSide.equals("RIGHT")?(tmp + fill):(fill + tmp);
	}

	/**
	 * Fill the object
	 * 
	 * @param size the final size
	 * @return the string filled
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
	 * @param pattern the pattern to analyze
	 * @return the precision
	 */
	public int getPrecision(String pattern) {
		int firstParen = pattern.indexOf(LBRAK);
		int lastParen = pattern.indexOf(RBRAK, firstParen);
		return (firstParen == -1 && lastParen == -1) ? pattern.length() : Integer.parseInt(pattern.substring(firstParen + 1, lastParen));
	}

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.formats.Format#parse(java.lang.String)
	 */
	public Object parse(String value) throws java.text.ParseException {
		return value.trim();
	}

	static public void main(String[] args) {
		AlphaNumeric alpha = new AlphaNumeric();
		alpha.applyPattern("X(4)");
		System.out.println ("#"+alpha.format("5180")+"#");
	}

}