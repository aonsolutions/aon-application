package com.code.aon.common.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

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
public class PrincipalUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalUtil.class.getName());
	
	/**
	 * Tell the JNDI subject name.
	 */
	private static final String SECURITY_SUBJECT = "java:comp/env/security/subject";

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