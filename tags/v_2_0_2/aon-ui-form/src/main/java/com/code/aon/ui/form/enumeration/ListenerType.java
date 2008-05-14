package com.code.aon.ui.form.enumeration;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum ListenerType represents the listener Types.
 */
public enum ListenerType implements IResourceable {
	
	/** BEFORE CREATED. */
	BEFORE_CREATED(0x1),
	
	/** AFTER CREATED. */
	AFTER_CREATED(0x2),

	/** BEFORE ADDED. */
	BEFORE_ADDED(0x4),
	
	/** AFTER ADDED. */
	AFTER_ADDED(0x8),
	
	/** BEFORE SELECTED. */
	BEFORE_SELECTED(0x10),
	
	/** AFTER SELECTED. */
	AFTER_SELECTED(0x20),

	/** BEFORE UPDATED. */
	BEFORE_UPDATED(0x40),
	
	/** AFTER UPDATED. */
	AFTER_UPDATED(0x80),
	
	/** BEFORE REMOVED. */
	BEFORE_REMOVED(0x100),
	
	/** AFTER REMOVED. */
	AFTER_REMOVED(0x200),
	
	/** BEFORE CANCELED. */
	BEFORE_CANCELED(0x400),
	
	/** AFTER CANCELED. */
	AFTER_CANCELED(0x800);

    /** Path of the message file */
    private static final String BASE_NAME = "com.code.aon.ui.form.i18n.messages";

    /** Messages key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_listenertype_";
	
	/** Value. */
	private int value;
	
	/**
	 * The Constructor.
	 * 
	 * @param value the value
	 */
	ListenerType( int value ) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	
    /**
     * Retuns the <code>String</code> translated to the <code>Locale</code>
     * pasado por parámetro.
     * 
     * @param locale the locale
     * 
     * @return String un <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
    /**
     * Return types as an array
     * 
     * @param types the types
     * 
     * @return the listener type[]
     */
    public static ListenerType[] toArray( int types ) {
    	List<ListenerType> result = new ArrayList<ListenerType>();
    	for( ListenerType type : values() ) {
    		if ( type.isIncluded(types) ) {
    			result.add( type );
    		}
    	}
    	return result.toArray( new ListenerType[result.size()] );
    }
	
    /**
     * return types as an int
     * 
     * @param types the types
     * 
     * @return the int
     */
    public static int toInt( ListenerType[] types ) {
    	int result = 0;
    	for( ListenerType type : types ) {
    		result |= type.getValue();
    	}
    	return result;
    }
    
    /**
     * Checks if is included.
     * 
     * @param value the value
     * 
     * @return true, if is included
     */
    public boolean isIncluded( int value ) {
    	return ( value & this.getValue() ) != 0;
    }
    
}
