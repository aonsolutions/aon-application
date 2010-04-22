package com.code.aon.common.enumeration;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Enumeration Decimal mask.
 * 
 * @author Consulting & Development. Aimar Tellitu - 17-mar-2006
 * @version 1.0
 * @since 1.0
 * 
 */

public enum DecimalMask implements IMask {

	/**
	 * TODO 
	 */
	DEFAULT ( null ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_0 ( "0" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_1 ( "0.00" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_2 ( "#,##0" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_3 ( "#,##0.0" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_4 ( "0'%'" ),	
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_5 ( "0.00'%'" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_6 ( "0.00E00" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_7 ( "000000" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_8 ( "#" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_9 ( "#.00" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_10 ( "#,###" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_11 ( "#,###.##" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_12 ( "#'%'" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_13 ( "#.##'%'" ),
	
	/**
	 * TODO 
	 */
	DECIMAL_MASK_14 ( "#.##E00" );

    /**
     * Messages file base path.
     */
    private static final String BASE_NAME = "com.code.aon.common.i18n.messages";

    /**
     * Messages key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_decimalmask_";

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
	DecimalMask( String pattern ) {
		this.pattern = pattern;
		if ( this.pattern != null ) {
			formatter = new DecimalFormat( this.pattern );
		} else {
			formatter = new DecimalFormat();
		}
	}

	/**
	 * Return the <code>DecimalMask</code>
	 * 
	 * @param value
	 * @return The <code>DecimalMask</code>.
	 */
    public static DecimalMask get(String value) {
    	for( DecimalMask _enum : values() ) {
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
