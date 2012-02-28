package com.code.aon.ui.common.controller;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.util.DomainUtil;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class DomainResolver.
 */
public class DomainResolver implements ILdapConstants, IAonObjectClasses {
	
    /**
     * Gets the domain.
     *
     * @param host the server name
     * @param skipLdap the skip ldap
     * @return the domain
     */
    public static String getDomain( String host, boolean skipLdap ) {
    	if (! skipLdap ) {
    		return DomainUtil.getDomain(host);
    	}
    	return host;    	
    }
    
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
    	return getDomain(AonUtil.getServerName(), AonUtil.isSkipLdap());
    }
    
}
