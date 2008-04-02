package com.code.aon.jaas.vendor.jboss;

import java.util.Map;
import java.util.Properties;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.AuthInfoLdap;
import com.code.aon.jaas.ldap.SecurityLdap;
import com.code.aon.ldap.LdapSession;

public class LdapLoginModule extends JBossLoginModule {
	
    /** Obtiene un logger apropiado. */
	protected static final Log LOGGER = LogFactory.getLog( LdapLoginModule.class.getName() );
	
	private Properties ldapProperties = new Properties();

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		super.initialize(subject, callbackHandler, sharedState, options);
		this.ldapProperties = new Properties();
		for( Object o : options.entrySet() ) {
			Map.Entry entry = (Map.Entry) o;
			String key = (String) entry.getKey();
			if ( key.startsWith("java.naming") ) {
				ldapProperties.put( key, entry.getValue() );
			}
		}
	}

	/**
     * Load Domain applications and users. 
	 * 
	 * @param domain
	 */
	@SuppressWarnings("unchecked")
	protected void load(String domain) {
        this.authInfo = new AuthInfoLdap( this.ldapProperties );
	}

	@Override
	protected void validateLoggedUsers() throws LoginException {
	}
	
}
