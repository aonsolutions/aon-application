package com.code.aon.common.enumeration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Enumeration date mask.
 * 
 * @author Consulting & Development. Aimar Tellitu - 17-mar-2006
 * @version 1.0
 * @since 1.0
 * 
 */

public enum DateMask implements IMask {

	/**
	 * TODO
	 */
	DEFAULT ( null ),
	
	/**
	 * TODO
	 */
	DD_MM_YY ( "dd/MM/yy" ),
	
	/**
	 * TODO
	 */
	MM_DD_YY ( "MM/dd/yy" ),
	
	/**
	 * TODO
	 */
	YY_MM_DD ( "yy/MM/dd" ),	
	
	/**
	 * TODO
	 */
	DD_MM_YYYY ( "dd/MM/yyyy" ),
	
	/**
	 * TODO
	 */
	MM_DD_YYYY ( "MM/dd/yyyy" ),
	
	/**
	 * TODO
	 */
	YYYY_MM_DD ( "yyyy/MM/dd" );
	
    /**
     * Messages file base path.
     */
    private static final String BASE_NAME = "com.code.aon.common.i18n.messages";

    /**
     * Messages key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_datemask_";

    /**
     * Pattern
     */
	private String pattern;

	/**
	 * Date formatter.
	 */
	private DateFormat formatter;

	/**
	 * Constructor.
	 * 
	 * @param pattern
	 */
	DateMask( String pattern ) {
		this.pattern = pattern;
		if ( this.pattern != null ) {
			formatter = new SimpleDateFormat( this.pattern );
		} else {
			formatter = DateFormat.getDateInstance();
		}
	}

	/**
	 * Return the <code>DateMask</code>
	 * 
	 * @param value
	 * @return The <code>DateMask</code>.
	 */
    public static DateMask get(String value) {
    	for( DateMask _enum : values() ) {
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

    /**
     * Return the Date formatter.
     * 
     * @return The Date formatter.
     */
	public SimpleDateFormat getFormatter() {
		return (SimpleDateFormat) this.formatter;
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
