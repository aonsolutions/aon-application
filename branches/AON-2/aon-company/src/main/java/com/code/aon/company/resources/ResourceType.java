package com.code.aon.company.resources;

import java.util.Locale;

import com.code.aon.common.enumeration.IResourceable;


/**
 * Company Resources enumeración. These can be an Employee, Desktop,...
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 03-oct-2007
 * @version 1.0
 * 
 * @since 1.0
 */
public enum ResourceType implements IResourceable {

	EMPLOYEE;

    /*(non-Javadoc)
     * @see com.code.aon.common.enumeration.IResourceable#getName(java.util.Locale)
     */
    public String getName(Locale locale) {
		return toString();
    }
   
}