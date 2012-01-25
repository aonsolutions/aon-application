package com.code.aon.common.util;

import java.security.Principal;

import org.apache.commons.lang.StringUtils;

/**
 * Esta clase implementa el interfaz <code>Principal</code> y representa un usuario.
 * <p>
 * Principals como <code>AuthPrincipal</code> se asocian con un <code>Subject</code> 
 * para añadir al <code>Subject</code> con una identidad adicional.
 * 
 * @see java.security.Principal
 * @see javax.security.auth.Subject
 * 
 * @author esferalia Networks. Aimar Tellitu - 23-ene-2012
 * @since 1.0
 */
public class BasicPrincipal implements Principal {

    /** Principal domain. ej: code.es */
    private String domain;
    
    /** Principal context */
    private String application;

    /**
     * Constructs an <code>AuthPrincipal</code>
     * <p>
     * @param name nombre del usuario.
	 */
	public BasicPrincipal(String domain, String application) {
		this.domain = domain;
		this.application = application;
	}

	/**
	 * @return Returns the domain.
	 */
	public String getDomain() {
		return domain;
	}

    /**
	 * @return Returns the application.
	 */
	public String getApplication() {
		return application;
	}

	/**
     * Returns a string representation of the object. 
     * <p>
     * @return string representation of the object.
     * @see java.security.Principal#toString()
     */
    public String toString() {
        return getName();
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
        return StringUtils.equals(anotherName, getName());
    }

    public int hashCode() {
        return getName().hashCode();
    }

	@Override
	public String getName() {
		return "aon@" + domain + "/" + application;
	}

}