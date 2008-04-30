package com.code.aon.ldap;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AbstractLdap {

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private static final Logger LOGGER = Logger.getLogger(AbstractLdap.class.getName());
	
	private Properties properties;
	
	private LdapSession session;
	
	public AbstractLdap(Properties properties) {
		this.properties = properties;
	}

	public LdapSession getLdapSession() throws LdapException {
		if ( this.session != null ) {
			closeSession();
		}
		this.session = new LdapSession();
		session.open(this.properties);
		return session;
	}
	
	public void closeSession() {
		try {
			if ( session != null ) {
				session.close();
			}
		} catch (LdapException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
	}
	
	public void delete( DistinguishedName dn ) throws LdapException {
		try {
			getLdapSession().delete(dn);
		} finally {
			closeSession();
		}
	}
	
	public boolean exists( DistinguishedName dn, String objectClass ) {
		boolean exists = false;
		try {
			exists = getLdapSession().exists( dn.toString(), LdapSession.getObjectClass(objectClass) );
		} catch ( LdapException e ) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		} finally {
			closeSession();
		}
		return exists;
	}
	
}
