package com.code.aon.common.enumeration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Enumeración para identificar los diferentes tipo de mascara para la hora.
 * 
 * @author Consulting & Development. Aimar Tellitu - 17-mar-2006
 * @version 1.0
 * 
 * @since 1.0
 */

public enum TimeMask implements IMask {

	/**
	 * TODO
	 */
	DEFAULT ( null ),
	
	/**
	 * TODO
	 */
	HH_MM_SS ( "HH:mm:ss" ),
	
	/**
	 * TODO
	 */
	HH_MM_SS_A ( "hh:mm:ss a" ),
	
	/**
	 * TODO
	 */
	HH_MM ( "HH:mm" ),	
	
	/**
	 * TODO
	 */
	HH_MM_A ( "hh:mm a" ),
	
	/**
	 * TODO
	 */
	HH ( "HH" ),
	
	/**
	 * TODO
	 */
	HH_A ( "hh a" );
	
    /**
     * Messages file base path.
     */
    private static final String BASE_NAME = "com.code.aon.common.i18n.messages";

    /**
     * Messages key prefix. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_timemask_";

    /**
     * Pattern
     */
	private String pattern;

	/**
	 * Date Formatter
	 */
	private DateFormat formatter;

	/**
	 * Constructor.
	 * 
	 * @param pattern
	 */
	TimeMask( String pattern ) {
		this.pattern = pattern;
		if ( this.pattern != null ) {
			formatter = new SimpleDateFormat( this.pattern );
		} else {
			formatter = DateFormat.getTimeInstance();
		}
	}

	/**
	 * Return the Time mask.
	 * 
	 * @param value
	 * @return The Time mask.
	 */
    public static TimeMask get(String value) {
    	for( TimeMask _enum : values() ) {
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
