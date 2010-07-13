package com.code.aon.company.resources;

import java.util.Locale;

import com.code.aon.common.enumeration.IResourceable;


/**
 * Company Resources enumeración. These can be an Employee, Desktop,...
 * 
 * @author Consulting & Development. Jorge Diez - 01-jul-2008
 * @version 1.0
 * 
 * @since 1.0
 */
public enum WebInfoPageType implements IResourceable {

	GENERIC,
	
	
	CONTACT,
	
	
	LOCATION,
	
	
	GALLERY;

    /*(non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getName(Locale locale) {
		return toString();
    }
   
}