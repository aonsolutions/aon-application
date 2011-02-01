package com.code.aon.csb.fd0.formats;

import java.text.DecimalFormat;

/**
 * Numeric Format
 * 
 * @author Consulting & Development. Iñigo GAyarre - 01/02/2007
 * @since 1.0
 *
 */
public class Numeric implements Format {

	public static final String SCALE_SEPARATOR = "V";
	public static final String DECIMAL_SEPARATOR = ".";
	public static final String ZERO = "0";
	public static final String PATTERN = "#";
	public static final int SCALE = 2;
	public static final int SIGN = 1;
	/**
	 * The pattern
	 */
	private Object pattern; // S9(?)V?
	/**
	 * The DecimalFormat
	 */
	private DecimalFormat decimalFormat; // ############.00
	/**
	 * The precision
	 */
	private int precision;
	/**
	 * The scale
	 */
	private int scale;
	/**
	 * The sign
	 */
	private boolean sign;
	/**
	 * Default value
	 */
	private static final String DEFAULT_VALUE = "0";

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.formats.Format#applyPattern(java.lang.Object)
	 */
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
		precision = getPrecision((String)this.pattern);
		scale = getScale((String)this.pattern);
		sign = getSign((String)this.pattern);
		decimalFormat = new DecimalFormat(this.fillPattern());
		decimalFormat.setMinimumFractionDigits(this.scale);
	}

	/**
	 * Returns the fill pattern
	 * 
	 * @return pattern string
	 */
	protected String fillPattern() {
		StringBuffer sb = new StringBuffer(this.precision + this.scale + 1);
		for (int i=0; i<this.precision; i++) {
			sb.append(PATTERN);
		}
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.formats.Format#format(java.lang.Object)
	 */
	public String format(Object value) {
		value = (value == null) ? new Double(DEFAULT_VALUE) : value;
		if (value instanceof String) value = new Double(value.toString());
		return this.format(decimalFormat.format(value));
	}

	/**
	 * Formats the String
	 * 
	 * @param value the original string
	 * @return the string formatted
	 */
	public String format(String value) {
		value = (value == null) ? DEFAULT_VALUE : value;

		String tmp = value.trim();
		boolean positive = (tmp.indexOf("-") == -1) ? true : false;
		String signValue = (sign) ? (positive) ? BLANK : "N" : "";
		tmp = (!positive) ? tmp.substring(1, tmp.length()) : tmp;
		String precision = "";
		String scale = "";
		if (tmp.indexOf(",") == -1 && tmp.indexOf(".") == -1) {
			precision = tmp.substring(0, tmp.length());
		} else {
				if (tmp.indexOf(",") > -1) {
					precision = tmp.substring(0, tmp.indexOf(","));
					scale = tmp.substring(tmp.indexOf(",") + 1, tmp.length());
				}
				if (tmp.indexOf(".") > -1) {
					precision = tmp.substring(0, tmp.indexOf("."));
					scale = tmp.substring(tmp.indexOf(".") + 1, tmp.length());
				}
		}
		String result = this.fillPrecision(precision.length()) + precision + scale;
		return signValue + result;
	}

	/**
	 * Return the needed string to fill the correct precision
	 * 
	 * @param size current size
	 * @return the filler string
	 */
	private String fillPrecision(int size) {
		int total = this.precision - size;
		StringBuffer sb = new StringBuffer(total);
		for (int i=0; i<total; i++) {
			sb.append(ZERO);
		}
		return sb.toString();
	}

	/**
	 * Returns decimal precision
	 *
	 * @param pattern the pattern
	 * @return the int precision
	 */
	public int getDecimalPrecision(String pattern) {
		int scaleParen = pattern.lastIndexOf(SCALE_SEPARATOR);
		String tmp;
		if (scaleParen == -1) {
			return 0;
		}
		else {
			tmp = new String(pattern.substring((scaleParen+1),pattern.length()).trim());
		}
		int firstParen = tmp.indexOf(LBRAK);
		int lastParen = tmp.indexOf(RBRAK, firstParen);
		if (firstParen==-1 && lastParen==-1) {
			if (getSign(tmp)) {
				return tmp.substring(1, tmp.length()).length();
			} else {
				return tmp.length();
			}
		}
		return Integer.parseInt( tmp.substring(firstParen + 1, lastParen) );
	}

	/**
	 * Returns the precision 
	 * 
	 * @param pattern the pattern
	 * @return the precision
	 */
	public int getPrecision(String pattern) {
		int scaleParen = pattern.indexOf(SCALE_SEPARATOR);
		String tmp;
		if (scaleParen == -1) {
			tmp = new String(pattern.trim());
		} else {
			tmp = new String(pattern.substring(0, scaleParen).trim());
		}
		int firstParen = tmp.indexOf(LBRAK);
		int lastParen = tmp.indexOf(RBRAK, firstParen);
		if (firstParen == -1 && lastParen == -1) {
			if (getSign(tmp)) {
				return tmp.substring(1, tmp.length()).length();
			} else {
				return tmp.length();
			}
		}
		return Integer.parseInt(tmp.substring(firstParen + 1, lastParen));
	}

	/**
	 * Returns the scale
	 * 
	 * @param pattern the pattern
	 * @return the scale
	 */
	public int getScale(String pattern) {
		int scaleParen = pattern.indexOf(SCALE_SEPARATOR);
		String tmp = new String(pattern.substring(scaleParen + 1, pattern.length()).trim());
		if (scaleParen == -1) {
			return 0;
		} else {
			int firstParen = tmp.indexOf(LBRAK);
			int lastParen = tmp.indexOf(RBRAK, firstParen);
			if (firstParen == -1 && lastParen == -1) {
				return tmp.length();
			}
			return Integer.parseInt(tmp.substring(firstParen + 1, lastParen));
		}
	}

	/**
	 * Returns if is signed pattern
	 * 
	 * @param pattern the pattern
	 * @return true if signed
	 */
	public boolean getSign(String pattern) {
		return pattern.startsWith("S");
	}

	/* (non-Javadoc)
	 * @see com.code.aon.csb.fd0.formats.Format#parse(java.lang.String)
	 */
	public Object parse(String value) throws java.text.ParseException {
		String original = value;
		if (getSign((String)this.pattern)) {
			if (value.substring(0,1) == "N") {
				value = new String ("-" + value.substring(1,value.length()));
			}
			else {
				value = new String (value.substring(1,value.length()));
			}
		}

		int precision = getDecimalPrecision((String)this.pattern);
		if (precision > 0) {
			StringBuffer result = new StringBuffer("");
			for (int i=0;i<value.length();i++) {
				if ( value.length() == (i+precision) ) {
					result.append(",");
				}
				result.append(value.charAt(i));
			}
			value = result.toString();
		}
		try {
			new Double( original );
		}
		catch ( NumberFormatException ex) {
			throw new NumberFormatException ( "Imposible convertir el valor a numérico [" + ex.getMessage()+ "]");
		}
		Object retValue = decimalFormat.parse(value);
		return retValue;
	}

	static public void main(String[] args) {
	  Numeric num = new Numeric();
	  num.applyPattern("9(8)V99");
	  Object str = new Double("-316.5457");
	  System.out.println (num.format(str));
	}

}