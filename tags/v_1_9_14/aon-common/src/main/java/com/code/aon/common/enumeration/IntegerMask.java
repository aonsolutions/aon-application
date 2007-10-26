package com.code.aon.common.enumeration;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Enumeration Integer mask.
 * 
 * @author Consulting & Development. Aimar Tellitu - 17-mar-2006
 * @version 1.0
 * 
 * @since 1.0
 */

public enum IntegerMask implements IMask {

	/**
	 * TODO 
	 */
	DEFAULT ( null ),
	
	/**
	 * TODO 
	 */
	INTEGER_MASK_0 ( "0" ),
	
	/**
	 * TODO 
	 */
	INTEGER_MASK_1 ( "#,##0" ),
	
	/**
	 * TODO 
	 */
	INTEGER_MASK_2 ( "00000" ),	
	
	/**
	 * TODO 
	 */
	INTEGER_MASK_3 ( "#" ),
	
	/**
	 * TODO 
	 */
	INTEGER_MASK_4 ( "#,###" );
	
    /**
     * Messages file base path.
     */
    private static final String BASE_NAME = "com.code.aon.common.i18n.messages";

    /**
     * Messages key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_integermask_";

    /**
     * Pattern
     */
	private String pattern;

	/**
	 * Date formatter.
	 */
	private DecimalFormat formatter;

	/**
	 * Constructor.
	 * 
	 * @param pattern
	 */
	IntegerMask( String pattern ) {
		this.pattern = pattern;
		if ( this.pattern != null ) {
			formatter = new DecimalFormat( this.pattern );
		} else {
			formatter = new DecimalFormat();
		}
	}

	/**
	 * Return the <code>IntegerMask</code>
	 * 
	 * @param value
	 * @return The <code>IntegerMask</code>.
	 */
    public static IntegerMask get(String value) {
    	for( IntegerMask _enum : values() ) {
    		if ( value != null ) {
	    		if ( value.equals(_enum.getPattern()) ) {
	    			return _enum;
	    		}
	    	} else if ( _enum.getPattern() == null ) {
	    		return _enum;
	    	}
    	}
		return null;
    }	

    /*
     * (non-Javadoc)
     * @see com.code.aon.common.enumeration.IMask#getPattern()
     */
	public String getPattern() {
		return this.pattern;
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
	 */
	public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	/*
	 * (non-Javadoc)
	 * @see com.code.aon.common.enumeration.IMask#format(java.lang.Object)
	 */
	public String format(Object value) {
		return formatter.format( value );
	}

}
