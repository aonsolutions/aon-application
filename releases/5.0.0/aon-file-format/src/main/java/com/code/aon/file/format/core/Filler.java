package com.code.aon.file.format.core;

/**
 * An interface of objects that can be formated
 *  
 * @author Consulting & Development. Iñigo GAyarre - 31/01/2007
 * @since 1.0
 *
 */
public interface Filler {

	/**
	 * Returns the object formated
	 * 
	 * @param value value to format
	 * @return value formated
	 */
	public Object format(Object value);

}
