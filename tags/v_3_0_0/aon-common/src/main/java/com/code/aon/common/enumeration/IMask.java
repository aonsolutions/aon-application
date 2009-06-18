package com.code.aon.common.enumeration;

import com.code.aon.common.enumeration.IResourceable;

/**
 * Interface that defines object mask, such as Decimal, Date, ...
 * 
 * @author Consulting & Development. 
 *
 */

public interface IMask extends IResourceable {

	/**
	 * Return the pattern.
	 * 
	 * @return The pattern.
	 */
	String getPattern();

	/**
	 * TODO
	 * @return int
	 * @deprecated
	 * 
	 */
	int ordinal();

	/**
	 * Format the object value.
	 *  
	 * @param value
	 * @return The formatted value.
	 */
	String format( Object value );
	
}
