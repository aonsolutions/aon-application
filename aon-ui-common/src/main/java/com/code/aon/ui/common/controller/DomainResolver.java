package com.code.aon.ui.common.controller;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

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
     * @param request the request
     * @return the domain
     */
    public static String getDomain( HttpServletRequest request ) {
    	String host = request.getServerName();
    	if (! AonUtil.isSkipLdap() ) {
    		host = DomainUtil.getDomain(host);
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
