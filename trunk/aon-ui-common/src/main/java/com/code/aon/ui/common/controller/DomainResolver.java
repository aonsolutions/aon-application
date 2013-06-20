package com.code.aon.ui.common.controller;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.util.AonUtil;

/**
 * The Class DomainResolver.
 */
public class DomainResolver {
	
	/**
	 * Gets the application name.
	 * 
	 * @param context the context
	 * @return the application name
	 */
	public static String getApplication( String context ) {
		return StringUtils.removeStart(context, "/");
	}  
   
    /**
     * Gets the domain.
     *
     * @return the domain
     */
    public String getDomain() {
    	return AonUtil.getServerName();
    }
    
}
