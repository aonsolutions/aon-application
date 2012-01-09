package com.code.aon.ui.common.controller;

import javax.faces.context.FacesContext;
import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import sun.net.util.IPAddressUtil;

import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class DomainResolver.
 */
public class DomainResolver implements ILdapConstants, IAonObjectClasses {
	
	/**
	 * Checks if is iP address.
	 *
	 * @param host the host
	 * @return true, if is iP address
	 */
	private static boolean isIPAddress( String host ) {
    	return IPAddressUtil.isIPv4LiteralAddress(host) || IPAddressUtil.isIPv6LiteralAddress(host);
    }
    
    /**
     * Exists domain.
     *
     * @param host the host
     * @return true, if successful
     */
    private static boolean existsDomain( String host ) {
    	BasicLdap ldap = new BasicLdap();
    	Name dn = NameResolver.getDomainDN(host);
    	return ldap.exists(dn, DOMAIN);
    }

    /**
     * Gets the domain.
     *
     * @param request the request
     * @return the domain
     */
    public static String getDomain( HttpServletRequest request ) {
    	String host = request.getServerName();
    	if (! (AonUtil.isSkipLdap() || isIPAddress(host)) ) {
    		if (! existsDomain(host) ) {
    			String domain = StringUtils.substringAfter(host, ".");
    			while (! StringUtils.isBlank(domain) ) {
    				if ( existsDomain(domain) ) {
    					host = domain;
    					break;
    				}
    				domain = StringUtils.substringAfter(domain, ".");
    			}
    		}
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
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
    	return getDomain(request);
    }
    
}
