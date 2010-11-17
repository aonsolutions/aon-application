package com.code.aon.jaas.auth;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.net.util.IPAddressUtil;

import com.code.aon.jaas.client.ast.IDomain;

/**
 * Esta clase implementa el interfaz <code>Principal</code> y representa un usuario.
 * <p>
 * Principals como <code>AuthPrincipal</code> se asocian con un <code>Subject</code> 
 * para añadir al <code>Subject</code> con una identidad adicional.
 * 
 * @version 1.4, 01/11/00
 * @see java.security.Principal
 * @see javax.security.auth.Subject
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 02-jun-2004
 * @since 1.0
 */
public class AuthPrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 1L;
    /** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuthPrincipal.class);

	/** Principal name. ej: shortName@domain/context */
    private String name;
    /** Principal shortName */
    private String shortName;
    /** Principal domain. ej: code.es */
    private String domain;
    /** Principal context */
    private String context;

    /**
     * Constructs an <code>AuthPrincipal</code>
     * <p>
     * @param name nombre del usuario.
	 */
	public AuthPrincipal(String name) {
        if (name == null) {
            throw new NullPointerException("illegal null input");
        }
        this.name = name;
        try {
	        this.shortName = user(name);
	        this.domain = domain(name);
	        this.context = context(name);
        } catch(IndexOutOfBoundsException e) {
        	this.shortName = name;
        }
	}

    /**
     * Returns user name: shortName@domain/context
     * <p>
     * @return user name
     * @see java.security.Principal#getName()
     */
    public String getName() {
        return name;
    }

	/**
	 * @return Returns the shortName.
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @return Returns the domain.
	 */
	public String getDomain() {
		return domain;
	}

    /**
	 * @return Returns the context.
	 */
	public String getContext() {
		return context;
	}

	/**
     * Returns a string representation of the object. 
     * <p>
     * @return string representation of the object.
     * @see java.security.Principal#toString()
     */
    public String toString() {
        return name;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Returns true 
     * if given object is equal tp this <code>AuthPrincipal</code> and both have 
     * the same user.
     * <p>
     * @param o the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the obj argument; 
     * 		<code>false</code> otherwise.
     * @see java.security.Principal#equals(Object)
     */
    public boolean equals(Object o) {
        if (!(o instanceof Principal)) {
            return false;
        }
        String anotherName = ((Principal) o).getName();
        boolean equals = false;
        if (name == null) {
            equals = anotherName == null;
        } else {
            equals = name.equals(anotherName);
        }

        return equals;
    }

    /*(non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Returns user: <b>shortName</b>@domain/context
     * 
     * @param value
     * @return
     */
    private String user(String value) {
        return value.substring( 0, value.lastIndexOf(IConstants.IDENTITY_SEPARATOR) );    	
    }
    
    /**
     * Checks if is iP address.
     * 
     * @param host the host
     * 
     * @return true, if is iP address
     */
    private boolean isIPAddress( String host ) {
    	return IPAddressUtil.isIPv4LiteralAddress(host) || IPAddressUtil.isIPv6LiteralAddress(host);
    }    

    /**
     * Returns user domain: user@<b>domain</b>/context
     * 
     * @param value
     * @return
     */
    private String domain(String value) {
    	String _domain = value.substring( value.indexOf(IConstants.IDENTITY_SEPARATOR) + 1, value.indexOf(IConstants.CONTEXT_SEPARATOR) ); 
		try {
			InetAddress thisIp = InetAddress.getLocalHost();
			LOGGER.debug( "Principal Domain: {} InetAddress[{}, {}, {}]",
							new Object[] {_domain, thisIp.getCanonicalHostName(), thisIp.getHostAddress(), thisIp.getHostName()} );
			if ( (_domain != null) && isIPAddress(_domain) ) {
				if ( thisIp.getHostAddress().equals(_domain) || IDomain.DEFAULT_DOMAIN_IP.equals(_domain) ) {
					_domain = IDomain.DEFAULT_DOMAIN_NAME;
				}
			}
		} catch(UnknownHostException e) {
			LOGGER.warn( e.getMessage(), e );
		}
    	return _domain; 
    }

    /**
     * Returns user context: user@domain/<b>context</b>
     * 
     * @param value
     * @return
     */
    private String context(String value) {
    	return value.substring( value.indexOf(IConstants.CONTEXT_SEPARATOR) );
    }
}