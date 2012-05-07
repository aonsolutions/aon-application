package com.code.aon.common.util;

import java.security.Principal;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.AuthPrincipal;

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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BasicPrincipal.class.getName());
	
	/**
	 * Tell the JNDI subject name.
	 */
	private static final String SECURITY_SUBJECT = "java:comp/env/security/subject";
	

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

	public static AuthPrincipal getAuthPrincipal() {
		InitialContext ic = null;
		try {
			ic = new InitialContext();
			Subject subject = (Subject) ic.lookup(SECURITY_SUBJECT);
            if (subject != null && subject.getPrincipals() != null) {
    			return (AuthPrincipal) subject.getPrincipals().iterator().next();
            }
		} catch (NamingException e) {
			LOGGER.error( "Error getting principal from " + SECURITY_SUBJECT, e );
		} finally {
			if ( ic != null) {
				try {
					ic.close();
				} catch (NamingException e) {
					LOGGER.error( "Error closing context " + SECURITY_SUBJECT, e );
				}
			}
		}
		return null;
	}
	
}