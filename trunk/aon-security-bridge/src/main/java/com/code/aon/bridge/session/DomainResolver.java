package com.code.aon.bridge.session;

import javax.faces.context.FacesContext;
import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import sun.net.util.IPAddressUtil;

import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;

public class DomainResolver implements ILdapConstants, IAonObjectClasses {
	
	public static final String CONTROLLER_NAME = "domainResolver";
	
    private static boolean isIPAddress( String host ) {
    	return IPAddressUtil.isIPv4LiteralAddress(host) || IPAddressUtil.isIPv6LiteralAddress(host);
    }
    
    private static boolean existsDomain( String host ) {
    	BasicLdap ldap = new BasicLdap();
    	Name dn = NameResolver.getDomainDN(host);
    	return ldap.exists(dn, DOMAIN);
    }
    
    public static String getDomain( HttpServletRequest request ) {
    	String host = request.getServerName();
    	if (! isIPAddress(host) ) {
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
    
    public String getDomain() {
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
    	return getDomain(request);
    }
    
}
