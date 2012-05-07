package com.code.aon.ldap.util;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN;

import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import sun.net.util.IPAddressUtil;

import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.NameResolver;

public class DomainUtil {

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
    private static boolean existsDomain( BasicLdap ldap, String host ) {
    	Name dn = NameResolver.getDomainDN(host);
    	return ldap.exists(dn, DOMAIN);
    }

    /**
     * Gets the domain.
     *
     * @param hostName
     * @return the domain
     */
    public static String getDomain( String hostName ) {
    	return getDomain( new BasicLdap(), hostName );    	
    }

    /**
     * Gets the domain.
     *
     * @param ldap
     * @param hostName
     * @return the domain
     */
    public static String getDomain( BasicLdap ldap, String hostName ) {
    	String host = hostName;
    	if (! isIPAddress(host) ) {
    		if (! existsDomain(ldap, host) ) {
    			String domain = StringUtils.substringAfter(host, ".");
    			while (! StringUtils.isBlank(domain) ) {
    				if ( existsDomain(ldap, domain) ) {
    					host = domain;
    					break;
    				}
    				domain = StringUtils.substringAfter(domain, ".");
    			}
    		}
    	}
    	return host;    	
    }    
    
}
