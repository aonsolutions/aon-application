package com.code.aon.bridge.session;

import javax.faces.context.FacesContext;
import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.net.util.IPAddressUtil;

import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.NameResolver;

public class DomainResolver implements ILdapConstants, IAonObjectClasses {

	private static final Log LOGGER = LogFactory.getLog( DomainResolver.class.getName() );	

    private static boolean isIPAddress( String host ) {
    	return IPAddressUtil.isIPv4LiteralAddress(host) || IPAddressUtil.isIPv6LiteralAddress(host);
    }
    
    private static boolean existsDomain( String host ) {
    	BasicLdap ldap = new BasicLdap();
    	Name dn = NameResolver.getDomainDN(host);
    	return ldap.exists(dn, DOMAIN);
    }
    
    private static String findDomain( String ipAddress ) {
    	BasicLdap ldap = new BasicLdap();
    	Name dn = NameResolver.getDomainsDN();
		try {
			String objectClass = NameResolver.getObjectClass(IAonObjectClasses.DOMAIN);
			String host = NameResolver.getEqualExpression(HOST_ATTRIBUTE, ipAddress);
			String filter = NameResolver.getAndExpression( host, objectClass );
			Entry entry = ldap.getLdapSession().searchOne(dn, filter, COMMON_NAME_ATTRIBUTE);
			if ( entry != null ) {
				return entry.getAsString(COMMON_NAME_ATTRIBUTE);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return null;
    }
    
    public static String getDomain( HttpServletRequest request ) {
    	String host = request.getServerName();
    	if ( isIPAddress(host) ) {
    		String domain = findDomain(host);
    		if ( domain != null ) {
    			host = domain;
    		}
    	} else {
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
